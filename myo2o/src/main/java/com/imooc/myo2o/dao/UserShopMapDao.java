package com.imooc.myo2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.myo2o.entity.UserShopMap;

public interface UserShopMapDao {
	/**
	 * 根据查询条件查询出用户店铺积分列表
	 * 
	 * @param userShopCondition
	 * @param rowIndex
	 * @param pageSize
	 * @return
	 */
	List<UserShopMap> queryUserShopMapList(@Param("userShopCondition") UserShopMap userShopCondition,
			@Param("rowIndex") int rowIndex, @Param("pageSize") int pageSize);

	/**
	 * 根据查询条件返回某一个顾客在某一个店铺的积分信息
	 * 
	 * @param userId
	 * @param shopId
	 * @return
	 */
	UserShopMap queryUserShopMap(@Param("userId") long userId, @Param("shopId") long shopId);

	/**
	 * 配合queryUserShopMapList方法返回用户店铺积分记录的总条数
	 * 
	 * @param userShopCondition
	 * @return
	 */
	int queryUserShopMapCount(@Param("userShopCondition") UserShopMap userShopCondition);

	/**
	 * 添加一条用户店铺积分记录
	 * 
	 * @param userShopMap
	 * @return
	 */
	int insertUserShopMap(UserShopMap userShopMap);

	/**
	 * 更新用户在某一个店铺的积分(比如购买了某一个商品,增加了积分,领取了某一个奖品,减少了积分都是通过这个方法来实现积分的增减)
	 * 
	 * @param userShopMap
	 * @return
	 */
	int updateUserShopMapPoint(UserShopMap userShopMap);

}
