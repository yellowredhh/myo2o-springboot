package com.imooc.myo2o.service;

import com.imooc.myo2o.dto.UserShopMapExecution;
import com.imooc.myo2o.entity.UserShopMap;

public interface UserShopMapService {
	/**
	 * 根据查询条件分页查询用户积分列表
	 * 
	 * @param userShopMapCondition
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	UserShopMapExecution listUserShopMap(UserShopMap userShopMapCondition, int pageIndex, int pageSize);

	/**
	 * 根据userId和shopId查询该用户在某一个店铺的积分情况
	 * 
	 * @param userId
	 * @param shopId
	 * @return
	 */
	UserShopMap getUserShopMap(long userId, long shopId);
}
