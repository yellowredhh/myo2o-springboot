package com.imooc.myo2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.myo2o.entity.ProductCategory;
@RunWith(SpringRunner.class)
@SpringBootTest
//将所有的方法按照方法名字的顺序执行.(在方法名字最前面加上a,b,c前缀就可以按照想要的顺序执行了)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductCategoryDaoTest{

	@Autowired
	private ProductCategoryDao productCategoryDao;

	@Test
	public void BqueryProductCategoryListTest() {
		Long shopId = 20L;
		List<ProductCategory> productCategoryList = productCategoryDao.queryProductCategoryList(shopId);
		System.out.println(productCategoryList.size());
	}

	@Test
	public void AbatchInsertProductCategoryTest() {
		ProductCategory pc1 = new ProductCategory();
		pc1.setProductCategoryName("商品类别1");
		//shopId是外键关联的,必须要死shop表中存在的值.
		pc1.setShopId(15L);
		pc1.setCreateTime(new Date());
		pc1.setPriority(55);

		ProductCategory pc2 = new ProductCategory();
		pc2.setProductCategoryName("商品类别2");
		pc2.setShopId(15L);
		pc2.setCreateTime(new Date());
		pc2.setPriority(55);

		List<ProductCategory> list = new ArrayList<ProductCategory>();
		list.add(pc1);
		list.add(pc2);
		int effectNumber = productCategoryDao.batchInsertProductCategory(list);
		assertEquals(2, effectNumber);
	}

	@Test
	public void CdeleteProductCategoryTest() {
		Long shopId = 15L;
		List<ProductCategory> list = productCategoryDao.queryProductCategoryList(shopId);
		for (ProductCategory productCategory : list) {
			if (productCategory.getProductCategoryName().equals("商品类别1")
					|| productCategory.getProductCategoryName().equals("商品类别2")) {
				int effectNumber = productCategoryDao.deleteProductCategory(productCategory.getProductCategoryId(), shopId);
				assertEquals(1, effectNumber);
			}
		}
	}

}
