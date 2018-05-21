package com.imooc.myo2o.dao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.myo2o.entity.Product;
import com.imooc.myo2o.entity.ProductSellDaily;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductSellDailyDaoTest {

	@Autowired
	private ProductSellDailyDao productSellDailyDao;

	@Test
	public void insertProductSellDailyTest() {
		int sellDaily = productSellDailyDao.insertProductSellDaily();
		System.out.println(sellDaily);
	}

	@Test
	public void queryProductSellDailyListTest() {
		ProductSellDaily productSellDailyCondition = new ProductSellDaily();
		Product product = new Product();
		product.setProductId(5L);
		productSellDailyCondition.setProduct(product);
		List<ProductSellDaily> queryProductSellDailyList = productSellDailyDao
				.queryProductSellDailyList(productSellDailyCondition, null, null);
		for (ProductSellDaily productSellDaily : queryProductSellDailyList) {
			System.out.println(productSellDaily.getProduct().getProductName());
		}
	}
}
