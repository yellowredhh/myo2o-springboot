package com.imooc.myo2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.myo2o.entity.UserProductMap;

public interface UserProductMapDao {
	/**
	 * 根据查询条件返回用户购买商品的信息列表
	 * @param userProductCondition
	 * @param rowIndex
	 * @param pageSize
	 * @return
	 */
	List<UserProductMap> queryUserProductMapList(
			@Param("userProductCondition") UserProductMap userProductCondition,
			@Param("rowIndex") int rowIndex, @Param("pageSize") int pageSize);

	/**
	 * 配合queryUserProductMapList方法获取列表子元素的个数
	 * @param userProductCondition
	 * @return
	 */
	int queryUserProductMapCount(
			@Param("userProductCondition") UserProductMap userProductCondition);

	/**
	 * 插入一条用户购买的商品的记录
	 * @param userProductMap
	 * @return
	 */
	int insertUserProductMap(UserProductMap userProductMap);
}
