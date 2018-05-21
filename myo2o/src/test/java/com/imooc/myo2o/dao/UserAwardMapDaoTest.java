package com.imooc.myo2o.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.myo2o.entity.UserAwardMap;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserAwardMapDaoTest {
	@Autowired
	private UserAwardMapDao userAwardMapDao;

	@Test
	public void testAInsertUserAwardMap() throws Exception {
		// 插入第一个奖品
		UserAwardMap userAwardMap = new UserAwardMap();
		userAwardMap.setUserId(8L);
		userAwardMap.setAwardId(9L);
		userAwardMap.setShopId(15L);
		userAwardMap.setUserName("test");
		userAwardMap.setAwardName("第一个奖品");
		userAwardMap.setCreateTime(new Date());
		userAwardMap.setUsedStatus(1);
		int effectedNum = userAwardMapDao.insertUserAwardMap(userAwardMap);
		assertEquals(1, effectedNum);
		// 插入第二个奖品
		userAwardMap.setUserId(8L);
		userAwardMap.setAwardId(9L);
		userAwardMap.setShopId(16L);
		userAwardMap.setUserName("test2");
		userAwardMap.setAwardName("第二个奖品");
		userAwardMap.setCreateTime(new Date());
		userAwardMap.setUsedStatus(0);
		effectedNum = userAwardMapDao.insertUserAwardMap(userAwardMap);
		assertEquals(1, effectedNum);
	}

	@Test
	public void testBQueryUserAwardMapList() throws Exception {
		UserAwardMap userAwardMap = new UserAwardMap();
		System.out.println("查询所有的奖品的前三个");
		List<UserAwardMap> userAwardMapList = userAwardMapDao.queryUserAwardMapList(userAwardMap, 0, 3);
		assertEquals(2, userAwardMapList.size());
		int count = userAwardMapDao.queryUserAwardMapCount(userAwardMap);
		assertEquals(2, count);

		System.out.println("查询名字中带有test的奖品的前三个");
		userAwardMap.setUserName("test");
		userAwardMapList = userAwardMapDao.queryUserAwardMapList(userAwardMap, 0, 3);
		assertEquals(2, userAwardMapList.size());
		count = userAwardMapDao.queryUserAwardMapCount(userAwardMap);
		assertEquals(2, count);

		System.out.println("查询userId为8的产品的前三个");
		userAwardMap.setUserId(8L);
		userAwardMapList = userAwardMapDao.queryUserAwardMapList(userAwardMap, 0, 3);
		assertEquals(2, userAwardMapList.size());
		count = userAwardMapDao.queryUserAwardMapCount(userAwardMap);
		assertEquals(2, count);

		System.out.println("查询shopId为15的产品的前三个");
		userAwardMap.setShopId(15L);
		userAwardMapList = userAwardMapDao.queryUserAwardMapList(userAwardMap, 0, 3);
		assertEquals(1, userAwardMapList.size());
		count = userAwardMapDao.queryUserAwardMapCount(userAwardMap);
		assertEquals(1, count);
	}
	
	@Test
	public void testCUpdateUserAwardMap() {
		UserAwardMap userAwardMap = new UserAwardMap();
		userAwardMap.setUserName("test");
		List<UserAwardMap> userAwardMapList = userAwardMapDao.queryUserAwardMapList(userAwardMap, 0, 3);
		assertEquals(2, userAwardMapList.size());
		userAwardMap = userAwardMapList.get(1);
		assertTrue("error,可用状态", 0 == userAwardMap.getUsedStatus());
		userAwardMap.setUsedStatus(1);
		int effectNumber = userAwardMapDao.updateUserAwardMap(userAwardMap);
		assertEquals(1, effectNumber);
	}
}
