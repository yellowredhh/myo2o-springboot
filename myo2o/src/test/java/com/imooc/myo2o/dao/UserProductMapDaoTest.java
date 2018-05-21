package com.imooc.myo2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.myo2o.entity.UserProductMap;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserProductMapDaoTest {
	@Autowired
	private UserProductMapDao userProductMapDao;
	@Autowired
	private ProductDao productDao;
	@Autowired
	private PersonInfoDao personInfoDao;

	@Test
	public void testAInsertUserProductMap() throws Exception {
		// 用户购买的第一个商品信息
		UserProductMap userProductMap = new UserProductMap();
		userProductMap.setUserId(8L);
		userProductMap.setProductId(6L);
		userProductMap.setShopId(16L);
		userProductMap.setUserName(personInfoDao.queryPersonInfoById(8L).getName());
		userProductMap.setProductName(productDao.queryProductByProductId(6L).getProductName());
		userProductMap.setCreateTime(new Date());
		int effectedNum = userProductMapDao.insertUserProductMap(userProductMap);
		assertEquals(1, effectedNum);
		// 用户购买的第二个商品信息
		userProductMap.setUserId(8L);
		userProductMap.setProductId(5L);
		userProductMap.setShopId(15L);
		userProductMap.setUserName(personInfoDao.queryPersonInfoById(8L).getName());
		userProductMap.setProductName(productDao.queryProductByProductId(5L).getProductName());
		userProductMap.setCreateTime(new Date());
		effectedNum = userProductMapDao.insertUserProductMap(userProductMap);
		assertEquals(1, effectedNum);
	}

	@Test
	public void testBQueryUserProductMapList() throws Exception {
		UserProductMap userProductMap = new UserProductMap();
		List<UserProductMap> userProductMapList = userProductMapDao.queryUserProductMapList(userProductMap, 0, 3);
		assertEquals(2, userProductMapList.size());
		int count = userProductMapDao.queryUserProductMapCount(userProductMap);
		assertEquals(2, count);

		userProductMap.setUserName("test");
		userProductMapList = userProductMapDao.queryUserProductMapList(userProductMap, 0, 3);
		assertEquals(2, userProductMapList.size());
		count = userProductMapDao.queryUserProductMapCount(userProductMap);
		assertEquals(2, count);

		userProductMap.setShopId(16L);
		userProductMapList = userProductMapDao.queryUserProductMapList(userProductMap, 0, 3);
		assertEquals(1, userProductMapList.size());
		count = userProductMapDao.queryUserProductMapCount(userProductMap);
		assertEquals(1, count);
	}
}
