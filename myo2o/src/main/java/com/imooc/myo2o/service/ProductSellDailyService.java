package com.imooc.myo2o.service;

import java.util.Date;
import java.util.List;

import com.imooc.myo2o.entity.ProductSellDaily;

public interface ProductSellDailyService {
	/**
	 * 每日定时对所有店铺的商品销量进行统计(即使是销量为零的商品也要进行统计,只是显示的柱状图的销量结果为零,不然柱状图中的顺序会错乱,因为我们是将每一个商品的一周的销量用一个数组传递到前台的)
	 * 
	 * @return
	 */
	void dailyCalculate();

	/**
	 * 根据查询条件返回商品日销售的统计列表
	 * 
	 * @param productSellDailyCondition
	 * @param beginTIme
	 * @param endTime
	 * @return
	 */
	List<ProductSellDaily> listProductSellDaily(ProductSellDaily productSellDailyCondition, Date beginTime,
			Date endTime);

}
