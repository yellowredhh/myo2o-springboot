package com.imooc.myo2o.service;

import com.imooc.myo2o.dto.UserAwardMapExecution;
import com.imooc.myo2o.entity.UserAwardMap;

public interface UserAwardMapService {

	/**
	 * 根据传入的查询条件分页获取映射列表以及列表总数;就是查询奖品兑换记录
	 * 
	 * @param userAwardCondition
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	UserAwardMapExecution listUserAwardMap(UserAwardMap userAwardCondition, Integer pageIndex, Integer pageSize);

	/**
	 * 根据userAwardMapId来获取映射信息(用于奖品详情页面)
	 * 
	 * @param userAwardMapId
	 * @return
	 */
	UserAwardMap getUserAwardMapById(long userAwardMapId);

	/**
	 * 添加用户奖品映射
	 * 
	 * @param userAwardMap
	 * @return
	 * @throws RuntimeException
	 */
	UserAwardMapExecution addUserAwardMap(UserAwardMap userAwardMap) throws RuntimeException;

	/**
	 * 修改用户奖品映射(主要用于修改领取奖品状态)
	 * 
	 * @param userAwardMap
	 * @return
	 * @throws RuntimeException
	 */
	UserAwardMapExecution modifyUserAwardMap(UserAwardMap userAwardMap) throws RuntimeException;

}
