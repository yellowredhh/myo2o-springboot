package com.imooc.myo2o.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.imooc.myo2o.dao.UserShopMapDao;
import com.imooc.myo2o.dto.UserShopMapExecution;
import com.imooc.myo2o.entity.UserShopMap;
import com.imooc.myo2o.service.UserShopMapService;
import com.imooc.myo2o.util.PageCalculator;

@Service
public class UserShopMapServiceImpl implements UserShopMapService {
	@Autowired
	private UserShopMapDao userShopMapDao;

	@Override
	public UserShopMapExecution listUserShopMap(UserShopMap userShopMapCondition, int pageIndex, int pageSize) {
		// 空值判定
		if (userShopMapCondition != null && pageIndex != -1 && pageSize != -1) {
			// 行页转换
			int beginIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
			// 根据查询条件分页查询
			List<UserShopMap> userShopMapList = userShopMapDao.queryUserShopMapList(userShopMapCondition, beginIndex,
					pageSize);
			// 基于同样的查询条件查询结果总数
			int count = userShopMapDao.queryUserShopMapCount(userShopMapCondition);
			UserShopMapExecution ue = new UserShopMapExecution();
			ue.setUserShopMapList(userShopMapList);
			ue.setCount(count);
			return ue;
		} else {
			return null;
		}

	}

	@Override
	public UserShopMap getUserShopMap(long userId, long shopId) {
		UserShopMap userShopMap = userShopMapDao.queryUserShopMap(userId, shopId);
		return userShopMap;
	}
}
