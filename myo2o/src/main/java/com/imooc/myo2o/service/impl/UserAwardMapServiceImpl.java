package com.imooc.myo2o.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.myo2o.dao.UserAwardMapDao;
import com.imooc.myo2o.dao.UserShopMapDao;
import com.imooc.myo2o.dto.UserAwardMapExecution;
import com.imooc.myo2o.entity.UserAwardMap;
import com.imooc.myo2o.entity.UserShopMap;
import com.imooc.myo2o.enums.UserAwardMapStateEnum;
import com.imooc.myo2o.service.UserAwardMapService;
import com.imooc.myo2o.util.PageCalculator;

@Service
public class UserAwardMapServiceImpl implements UserAwardMapService {
	@Autowired
	private UserAwardMapDao userAwardMapDao;
	@Autowired
	private UserShopMapDao userShopMapDao;

	@Override
	public UserAwardMapExecution listUserAwardMap(UserAwardMap userAwardCondition, Integer pageIndex,
			Integer pageSize) {
		if (userAwardCondition != null && pageIndex != null && pageSize != null) {
			int beginIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
			// 根据查询条件分页查询查询用户奖品映射(查询用户奖品兑换记录)
			List<UserAwardMap> userAwardMapList = userAwardMapDao.queryUserAwardMapList(userAwardCondition, beginIndex,
					pageSize);
			// 基于同样的条件获取查询结果总数
			int count = userAwardMapDao.queryUserAwardMapCount(userAwardCondition);
			UserAwardMapExecution ue = new UserAwardMapExecution();
			ue.setUserAwardMapList(userAwardMapList);
			ue.setCount(count);
			return ue;
		} else {
			return null;
		}

	}

	@Override
	public UserAwardMap getUserAwardMapById(long userAwardMapId) {
		return userAwardMapDao.queryUserAwardMapById(userAwardMapId);
	}

	@Override
	@Transactional
	public UserAwardMapExecution addUserAwardMap(UserAwardMap userAwardMap) throws RuntimeException {
		// 空值判定
		if (userAwardMap != null && userAwardMap.getUserId() != null && userAwardMap.getShopId() != null) {
			// 设置默认值
			userAwardMap.setCreateTime(new Date());
			try {
				int effectedNum = 0;
				// 判断当前要兑换的奖品是否需要积分,如果需要积分,则将tb_user_map对应的用户积分进行抵扣
				if (userAwardMap.getPoint() != null && userAwardMap.getPoint() > 0) {
					// 根据userId和shopId去查询单个的用户商品映射信息(就是用户在该店铺消费产生了多少积分)
					UserShopMap userShopMap = userShopMapDao.queryUserShopMap(userAwardMap.getUserId(),
							userAwardMap.getShopId());
					// 如果用户在这个店铺消费过(也就是判断该用户在这个店铺是否有积分)
					if (userShopMap != null) {
						// 如果用户在这个店铺消费过,且消费产生的积分大于当前要换的奖品所需要的积分,则可以进行奖品兑换
						if (userShopMap.getPoint() >= userAwardMap.getPoint()) {
							// 积分抵扣,兑换奖品,在总积分上减去对应的积分
							userShopMap.setPoint(userShopMap.getPoint() - userAwardMap.getPoint());
							// 更新用户商铺积分映射(将减少的积分更新到数据库中)
							effectedNum = userShopMapDao.updateUserShopMapPoint(userShopMap);
							if (effectedNum <= 0) {
								throw new RuntimeException("更新积分信息失败");
							}
						} else {
							throw new RuntimeException("积分不足无法领取");
						}

					} else {
						// 在店铺没有积分,抛出异常
						throw new RuntimeException("在本店铺没有积分，无法对换奖品");
					}
				}
				// 在用户奖品映射中添加用户兑换到的奖品
				effectedNum = userAwardMapDao.insertUserAwardMap(userAwardMap);
				if (effectedNum <= 0) {
					throw new RuntimeException("领取奖励失败");
				}

				return new UserAwardMapExecution(UserAwardMapStateEnum.SUCCESS, userAwardMap);
			} catch (Exception e) {
				throw new RuntimeException("领取奖励失败:" + e.toString());
			}
		} else {
			return new UserAwardMapExecution(UserAwardMapStateEnum.NULL_USERAWARD_INFO);
		}
	}

	@Override
	@Transactional
	public UserAwardMapExecution modifyUserAwardMap(UserAwardMap userAwardMap) throws RuntimeException {
		// 空值判定,检查userAwardId是否为空,usedStatus是否为可用状态
		if (userAwardMap == null || userAwardMap.getUserAwardId() == null || userAwardMap.getUsedStatus() == null) {
			return new UserAwardMapExecution(UserAwardMapStateEnum.NULL_USERAWARD_ID);
		} else {
			try {
				// 更新可用状态
				int effectedNum = userAwardMapDao.updateUserAwardMap(userAwardMap);
				if (effectedNum <= 0) {
					return new UserAwardMapExecution(UserAwardMapStateEnum.INNER_ERROR);
				} else {
					return new UserAwardMapExecution(UserAwardMapStateEnum.SUCCESS, userAwardMap);
				}
			} catch (Exception e) {
				throw new RuntimeException("modifyUserAwardMap error: " + e.getMessage());
			}
		}
	}

}
