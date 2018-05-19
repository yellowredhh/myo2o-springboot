package com.imooc.myo2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.myo2o.entity.Shop;

public interface ShopDao {

	/*
	 *如果不加上@Param("shopCondition") 会有如下错误 Caused by: org.apache.ibatis.reflection.ReflectionException: There is no getter for property named 'shopCondition' in 'class com.imooc.myo2o.entity.Shop'
	 * 查询商铺的总数.
	 */
	public int queryShopCount(@Param("shopCondition") Shop shopCondition);

	/* 
	 * 高级查询和枫叶查询:
	 * 根据给定的shop信息去查询所有符合这个给定信息的对应的所有的shop,在shopCondition中可以添加各种查询条件.
	 * 后两个参数用于实现分页查询.
	 * rowIndex表示从第几行开始取数据.
	 * pageSize一个页面返回的条数.
	 */
	public List<Shop> queryShopList(@Param("shopCondition") Shop shopCondition, @Param("rowIndex") int rowIndex,
			@Param("pageSize") int pageSize);

	/*
	 * 根据给定的shopId去查询对应的shop消息.
	 */
	public Shop queryShopByShopId(Long shopId);

	/*
	 * 插入shop,如果返回1则插入成功,如果返回-1,则失败,这个-1是mybatis返回的.
	 */
	public int insertShop(Shop shop);

	public int updateShop(Shop shop);

	public int deleteShop(Shop shop);

	/**
	 * 通过employee id 查询店铺
	 * 
	 * @param employeeId
	 * @return List<shop>
	 */
	List<Shop> queryByEmployeeId(long employeeId);

}
