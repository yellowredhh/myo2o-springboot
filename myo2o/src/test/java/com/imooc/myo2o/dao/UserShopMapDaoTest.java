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

import com.imooc.myo2o.entity.UserShopMap;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class UserShopMapDaoTest {
	@Autowired
	private UserShopMapDao userShopMapDao;

	@Test
	public void testInsertUserShopMap() {
		// 添加一条用户商铺映射记录
		UserShopMap userShopMap1 = new UserShopMap();
		userShopMap1.setUserId(8L);
		userShopMap1.setShopId(15L);
		userShopMap1.setPoint(1);
		userShopMap1.setCreateTime(new Date());
		int effectNumber1 = userShopMapDao.insertUserShopMap(userShopMap1);
		assertEquals(1, effectNumber1);
		// 添加一条用户商铺映射记录
		UserShopMap userShopMap2 = new UserShopMap();
		userShopMap2.setUserId(8L);
		userShopMap2.setShopId(16L);
		userShopMap2.setPoint(2);
		userShopMap2.setCreateTime(new Date());
		int effectNumber2 = userShopMapDao.insertUserShopMap(userShopMap2);
		assertEquals(1, effectNumber2);
	}

	@Test
	public void testQueryUserShopMapList() {
		// 根据userId去查询用户积分情况
		UserShopMap userShopMap = new UserShopMap();
		userShopMap.setUserId(8L);
		List<UserShopMap> userShopMapList = userShopMapDao.queryUserShopMapList(userShopMap, 0, 3);
		assertEquals(2, userShopMapList.size());
		// 根据userId和shopId去查询用户积分情况
		userShopMap.setShopId(16L);
		userShopMapList = userShopMapDao.queryUserShopMapList(userShopMap, 0, 3);
		assertEquals(1, userShopMapList.size());
		System.out.println(userShopMapList.get(0).getPoint());
	}

	@Test
	public void testUpdateUserShopMapPoint() {
		UserShopMap userShopMap = userShopMapDao.queryUserShopMap(8L, 16L);
		System.out.println(userShopMap.getPoint());
		userShopMap.setPoint(5);
		int effectNumber = userShopMapDao.updateUserShopMapPoint(userShopMap);
		assertEquals(1, effectNumber);
	}
}
