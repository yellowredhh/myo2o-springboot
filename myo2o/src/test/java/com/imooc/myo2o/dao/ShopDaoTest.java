package com.imooc.myo2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.entity.ShopCategory;
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopDaoTest{
	@Autowired
	private ShopDao shopdao;
	
	/**
	 * 查询shop不能使用shop的shopCategory的parentId来查询,因为dao层没有实现这种方式.
	 * 但是查询shopCategory是可以使用shopCategory的parentId来出现的,dao层实现了这种方式
	 */
	@Test
	public void queryShopListAndCountTest() {
		//需求:查询某一个一级店铺类别下面的所有的店铺;两种实现方式
		//第一种方式,对shop对象的shopCategory属性的parentId设置值.(由于我的dao层代码没有实现shopCategory的parentid来实现,所以需要更改后台dao查询代码来实现)
		ShopCategory shopCategory = new ShopCategory();
		//一级店铺id为10
		shopCategory.setParentId(10L);
		Shop shopCondition = new Shop();
		shopCondition.setShopCategory(shopCategory);

		//第二种方式:可以直接对shop对象设置一级店铺.
		ShopCategory parentCategory = new ShopCategory();
		parentCategory.setShopCategoryId(10L);
		shopCondition.setParentCategory(parentCategory);
		List<Shop> shopList = shopdao.queryShopList(shopCondition, 0, 3);
		int count = shopdao.queryShopCount(shopCondition);
		System.out.println(shopList.size());
		System.out.println("店铺总数:" + count);
	}

	@Test
	public void queryShopByShopIdTest() {
		//数据库中没有这个shopId为1的数据行,查询不报错,给输出了一个null.
		Long shopId = 49l;
		Shop shop = shopdao.queryShopByShopId(shopId);
		System.out.println(shop);
	}

	@Test
	public void insertShopTest() {
		Shop shop = new Shop();
		shop.setOwnerId(9L);
		shop.setShopName("will");
		shop.setEnableStatus(0);
		int test = shopdao.insertShop(shop);
		assertEquals(1, test);
	}

	@Test
	public void updateShopTest() {
		Shop shop = new Shop();
		shop.setShopId(29L);
		shop.setShopAddr("huanghong");
		int effectNumber = shopdao.updateShop(shop);
		assertEquals(1, effectNumber);
	}

	@Test
	public void deleteShopTest() {
		Shop shop = new Shop();
		shop.setShopId(30L);
		int effectNumber = shopdao.deleteShop(shop);
		assertEquals(1, effectNumber);
	}

}
