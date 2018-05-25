package com.imooc.myo2o.service;

import com.imooc.myo2o.dto.UserProductMapExecution;
import com.imooc.myo2o.entity.UserProductMap;

public interface UserProductMapService {
	/**
	 * 根据查询条件获取消费记录
	 * 
	 * @param shopId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	UserProductMapExecution listUserProductMap(UserProductMap userProductCondition, Integer pageIndex,
			Integer pageSize);

	/**
	 * 添加消费记录
	 * 
	 * @param userProductMap
	 * @return
	 * @throws RuntimeException
	 */
	UserProductMapExecution addUserProductMap(UserProductMap userProductMap) throws RuntimeException;

}
