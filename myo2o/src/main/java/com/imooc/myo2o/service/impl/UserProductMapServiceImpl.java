package com.imooc.myo2o.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.myo2o.dao.PersonInfoDao;
import com.imooc.myo2o.dao.ShopDao;
import com.imooc.myo2o.dao.UserProductMapDao;
import com.imooc.myo2o.dao.UserShopMapDao;
import com.imooc.myo2o.dto.UserProductMapExecution;
import com.imooc.myo2o.entity.PersonInfo;
import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.entity.UserProductMap;
import com.imooc.myo2o.entity.UserShopMap;
import com.imooc.myo2o.enums.UserProductMapStateEnum;
import com.imooc.myo2o.service.UserProductMapService;
import com.imooc.myo2o.util.PageCalculator;

@Service
public class UserProductMapServiceImpl implements UserProductMapService {
	@Autowired
	private UserProductMapDao userProductMapDao;
	@Autowired
	private UserShopMapDao userShopMapDao;
	@Autowired
	private PersonInfoDao personInfoDao;
	@Autowired
	private ShopDao shopDao;

	@Override
	public UserProductMapExecution listUserProductMap(UserProductMap userProductCondition, Integer pageIndex,
			Integer pageSize) {
		// 空值判定
		if (userProductCondition != null && pageIndex != null && pageSize != null) {
			// 页转行
			int beginIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
			// 传入查询条件进行高级查询,分页查询
			List<UserProductMap> userProductMapList = userProductMapDao.queryUserProductMapList(userProductCondition,
					beginIndex, pageSize);
			// 基于同样的查询条件来查询结果总数
			int count = userProductMapDao.queryUserProductMapCount(userProductCondition);
			UserProductMapExecution se = new UserProductMapExecution();
			se.setUserProductMapList(userProductMapList);
			se.setCount(count);
			return se;
		} else {
			return null;
		}

	}

	@Override
	@Transactional
	public UserProductMapExecution addUserProductMap(UserProductMap userProductMap) throws RuntimeException {
		// 空值判定,确保顾客id,店铺id非空
		if (userProductMap != null && userProductMap.getUserId() != null && userProductMap.getShopId() != null) {
			// 设置默认值
			userProductMap.setCreateTime(new Date());
			try {
				// 添加消费记录
				int effectedNum = userProductMapDao.insertUserProductMap(userProductMap);
				if (effectedNum <= 0) {
					throw new RuntimeException("添加消费记录失败");
				}
				// 若本次消费能够产生积分
				if (userProductMap.getPoint() != null && userProductMap.getPoint() > 0) {
					// 查询该顾客是否在该店铺消费过
					UserShopMap userShopMap = userShopMapDao.queryUserShopMap(userProductMap.getUserId(),
							userProductMap.getShopId());
					if (userShopMap != null) {
						// 如果顾客之前在本店铺消费过,则有过积分记录,就进行总积分的更新操作
						if (userShopMap.getPoint() >= userProductMap.getPoint()) {// 这一段判定是什么操作??
							userShopMap.setPoint(userShopMap.getPoint() + userProductMap.getPoint());
							effectedNum = userShopMapDao.updateUserShopMapPoint(userShopMap);
							if (effectedNum <= 0) {
								throw new RuntimeException("更新积分信息失败");
							}
						}
					} else {
						// 在店铺没有过消费记录，添加一条积分信息
						userShopMap = compactUserShopMap4Add(userProductMap.getUserId(), userProductMap.getShopId(),
								userProductMap.getPoint());
						effectedNum = userShopMapDao.insertUserShopMap(userShopMap);
						if (effectedNum <= 0) {
							throw new RuntimeException("积分信息创建失败");
						}
					}
				}
				return new UserProductMapExecution(UserProductMapStateEnum.SUCCESS, userProductMap);
			} catch (Exception e) {
				throw new RuntimeException("添加授权失败:" + e.toString());
			}
		} else {
			return new UserProductMapExecution(UserProductMapStateEnum.NULL_USERPRODUCT_INFO);
		}
	}

	/**
	 * 对userId,shopId,point进行封装
	 * 
	 * @param userId
	 * @param shopId
	 * @param point
	 * @return
	 */
	private UserShopMap compactUserShopMap4Add(Long userId, Long shopId, Integer point) {
		UserShopMap userShopMap = null;
		// 空值判定
		if (userId != null && shopId != null) {
			userShopMap = new UserShopMap();
			PersonInfo personInfo = personInfoDao.queryPersonInfoById(userId);
			Shop shop = shopDao.queryShopByShopId(shopId);
			userShopMap.setUserId(userId);
			userShopMap.setShopId(shopId);
			userShopMap.setUserName(personInfo.getName());
			userShopMap.setShopName(shop.getShopName());
			userShopMap.setCreateTime(new Date());
			userShopMap.setPoint(point);
		}
		return userShopMap;
	}

}
