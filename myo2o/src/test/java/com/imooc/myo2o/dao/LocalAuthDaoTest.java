package com.imooc.myo2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.myo2o.entity.LocalAuth;
import com.imooc.myo2o.entity.PersonInfo;
@RunWith(SpringRunner.class)
@SpringBootTest
public class LocalAuthDaoTest{

	private static final String username = "huanghong";
	private static final String password = "testpassword";
	@Autowired
	private LocalAuthDao localAuthDao;

	/**
	 * 根据传入的用户名和密码查找localAuth帐号
	 */
	@Test
	public void queryLocalAuthByUserNameAndPwd() {
		LocalAuth localAuth = localAuthDao.queryLocalByUserNameAndPwd("xiangze", "s05bse6q2qlb9qblls96s592y55y556s");
		System.out.println(localAuth);
	}

	/**
	 * 根据userId查询对应的本地账号
	 */
	@Test
	public void queryLocalAuthByUserId() {
		LocalAuth localAuth = localAuthDao.queryLocalByUserId(8L);
		System.out.println(localAuth);
	}

	/**
	 * 新增本地账号,本地账号和personinfo是一一对应的关系,username是具有唯一性,所以不可以插入两个username一样的数据
	 */
	@Test
	public void insertLocalAuthTest() {
		PersonInfo personInfo = new PersonInfo();
		personInfo.setGender("男");
		personInfo.setUserId(11L);
		LocalAuth localAuth = new LocalAuth();
		localAuth.setCreateTime(new Date());
		localAuth.setLastEditTime(new Date());
		localAuth.setUserName(username);//数据库中这个userName列是有唯一性的.
		localAuth.setPassword(password);
		localAuth.setUserId(11L);
		int effectNumber = localAuthDao.insertLocalAuth(localAuth);
		assertEquals(1, effectNumber);
	}

	/**
	 * 根据userId,userName,password来修改密码
	 */
	@Test
	public void updateLocalAuthTest() {
		int effectNumber = localAuthDao.updateLocalAuth(11L, username, password, "new" + password, new Date());
		assertEquals(1, effectNumber);
		LocalAuth localAuth = localAuthDao.queryLocalByUserId(11L);
		System.out.println(localAuth.getPassword());
	}
}
