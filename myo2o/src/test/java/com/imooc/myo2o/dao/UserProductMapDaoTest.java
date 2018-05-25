package com.imooc.myo2o.dao;

import static org.junit.Assert.assertEquals;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

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
		// 同一个店铺的三种商品的近一周的销量
		for (long productId = 10; productId < 13; productId++) {
			for (int i = 1; i < 8; i++) {
				Calendar calendar = Calendar.getInstance();
				calendar.add(calendar.DATE, -i);
				Date date = calendar.getTime();
				int random = new Random().nextInt(10);
				UserProductMap userProductMap = new UserProductMap();
				userProductMap.setUserId(8L);
				userProductMap.setProductId(productId);
				userProductMap.setShopId(20L);
				userProductMap.setUserName(personInfoDao.queryPersonInfoById(8L).getName());
				userProductMap.setProductName(productDao.queryProductByProductId(productId).getProductName());
				userProductMap.setCreateTime(date);
				int effectedNum = 0;
				for (int j = 0; j < random; j++) {
					effectedNum = userProductMapDao.insertUserProductMap(userProductMap);
					effectedNum++;
				}
				System.out.println("商品id为" + productId + "在" + date + "的销量是" + effectedNum);
			}
		}
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
