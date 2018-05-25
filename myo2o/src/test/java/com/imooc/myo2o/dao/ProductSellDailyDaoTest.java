package com.imooc.myo2o.dao;

import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.myo2o.entity.Product;
import com.imooc.myo2o.entity.ProductSellDaily;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductSellDailyDaoTest {

	@Autowired
	private ProductSellDailyDao productSellDailyDao;

	@Test
	public void AinsertAProductSellDailyTest() {
		int sellDaily = productSellDailyDao.insertProductSellDaily();
		System.out.println(sellDaily);
	}

	@Test
	public void BinserDefaultProductSellDailyTest() {
		int defaultProductSellDaily = productSellDailyDao.insertDefaultProductSellDaily();
		System.out.println(defaultProductSellDaily);
	}

	@Test
	public void CqueryProductSellDailyListTest() {
		ProductSellDaily productSellDailyCondition = new ProductSellDaily();
		Product product = new Product();
		product.setProductId(5L);
		productSellDailyCondition.setProduct(product);
		List<ProductSellDaily> queryProductSellDailyList = productSellDailyDao
				.queryProductSellDailyList(productSellDailyCondition, null, new Date());
		for (ProductSellDaily productSellDaily : queryProductSellDailyList) {
			System.out.println(productSellDaily.getProduct().getProductName());
		}
	}
}
