package com.imooc.myo2o.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.myo2o.entity.ProductSellDaily;

public interface ProductSellDailyDao {
	/**
	 * 根据查询条件返回商品日销售的统计列表(这个查询条件可以是某一个商品,或者某一个商铺,或者是开始时间,或者是结束时间,或者是四者的排列组合)
	 * 
	 * @param productSellDailyCondition
	 * @param beginTime
	 * @param endTime
	 * @return
	 */
	List<ProductSellDaily> queryProductSellDailyList(
			@Param("productSellDailyCondition") ProductSellDaily productSellDailyCondition,
			@Param("beginTime") Date beginTime, @Param("endTime") Date endTime);

	/**
	 * 查询整个平台一天之内出售的商品(根据userProductMap表的变化进行查询的,查询结果添加到productSellDaily表中)
	 * 
	 * @return
	 */
	int insertProductSellDaily();
}
