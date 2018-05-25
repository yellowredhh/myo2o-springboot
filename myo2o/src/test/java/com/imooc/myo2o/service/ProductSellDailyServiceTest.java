package com.imooc.myo2o.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.myo2o.entity.ProductSellDaily;
import com.imooc.myo2o.entity.Shop;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductSellDailyServiceTest {

	@Autowired
	private ProductSellDailyService productSellDailyService;

	@Test
	public void AtestDailyCalculate() {
		productSellDailyService.dailyCalculate();
	}

	/**
	 * 统计最近一周的商品的销量
	 */
	@Test
	public void BtestListProductSellDailyTest() {
		Shop currentShop = new Shop();
		currentShop.setShopId(20L);
		ProductSellDaily productSellDailyCondition = new ProductSellDaily();
		productSellDailyCondition.setShop(currentShop);
		Calendar calendar = Calendar.getInstance();
		// 获取昨天的日期
		calendar.add(Calendar.DATE, -1);
		Date endTime = calendar.getTime();
		// 获取七天之前的日期:由于之前已经减去了一天,所以这里再减去六天,获取到的是七天之前的日期
		calendar.add(Calendar.DATE, -6);
		Date beginTime = calendar.getTime();
		// 传入查询条件进行销量查询(销量为0的也会被查出来,因为在productSellDaily表格中添加了销量为零的商品的信息
		List<ProductSellDaily> productSellDailyList = productSellDailyService
				.listProductSellDaily(productSellDailyCondition, beginTime, endTime);
		System.out.println(productSellDailyList.size());
	}
}
