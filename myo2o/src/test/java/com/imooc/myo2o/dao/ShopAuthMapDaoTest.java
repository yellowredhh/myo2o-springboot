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

import com.imooc.myo2o.entity.ShopAuthMap;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ShopAuthMapDaoTest {
	@Autowired
	private ShopAuthMapDao shopAuthMapDao;

	@Test
	public void testAInsertShopAuthMap() throws Exception {
		ShopAuthMap shopAuthMap = new ShopAuthMap();
		shopAuthMap.setEmployeeId(8L); // 这个employeeId不要乱写,这个和PersonInfo中userId是外键关联关系
		shopAuthMap.setShopId(15L); // shopId和Shop表格中shopId外键关联
		shopAuthMap.setName("test1");
		shopAuthMap.setTitle("CEO");
		shopAuthMap.setTitleFlag(1);
		shopAuthMap.setCreateTime(new Date());
		shopAuthMap.setLastEditTime(new Date());
		shopAuthMap.setEnableStatus(1);
		int effectedNum = shopAuthMapDao.insertShopAuthMap(shopAuthMap);
		assertEquals(1, effectedNum);
	}

	@Test
	public void testBQueryShopAuthMapListByShopId() throws Exception {
		List<ShopAuthMap> shopAuthMapList = shopAuthMapDao.queryShopAuthMapListByShopId(15, 0, 3);
		assertEquals(1, shopAuthMapList.size());
		shopAuthMapList = shopAuthMapDao.queryShopAuthMapListByShopId(28, 0, 3);
		assertEquals(1, shopAuthMapList.size());
		int count = shopAuthMapDao.queryShopAuthCountByShopId(28);
		assertEquals(1, count);
	}

	@Test
	public void testCUpdateShopAuthMap() throws Exception {
		ShopAuthMap shopAuthMap = new ShopAuthMap();
		shopAuthMap.setShopAuthId(27L);
		shopAuthMap.setShopId(15L);
		shopAuthMap.setTitle("CCO");
		shopAuthMap.setTitleFlag(2);
		int effectedNum = shopAuthMapDao.updateShopAuthMap(shopAuthMap);
		assertEquals(1, effectedNum);
	}

	@Test
	public void testDeleteShopAuthMap() throws Exception {
		long shopAuthId = 26L;
		long shopId = 28L;
		int effectedNum = shopAuthMapDao.deleteShopAuthMap(shopAuthId, shopId);
		assertEquals(1, effectedNum);
	}
}
