package com.imooc.myo2o.dao;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.myo2o.entity.ShopCategory;
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopCategoryDaoTest{

	@Autowired
	private ShopCategoryDao shopCategoryDao;
	
	/**
	 * 查询shop不能使用shop的shopCategory的parentId来查询,因为dao层没有实现这种方式.
	 * 但是查询shopCategory是可以使用shopCategory的parentId来出现的,dao层实现了这种方式
	 */
	@Test
	public void queryShopCategoryDaoTest() {
		ShopCategory shopCategory = new ShopCategory();
		shopCategory.setParentId(11L);
		//List<ShopCategory> shopCategoryList = shopCategoryDao.queryShopCategory(null);
		List<ShopCategory> shopCategoryList = shopCategoryDao.queryShopCategory(shopCategory);
		System.out.println(shopCategoryList);
	}
}
