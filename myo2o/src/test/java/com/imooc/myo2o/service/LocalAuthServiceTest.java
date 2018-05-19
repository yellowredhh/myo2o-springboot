package com.imooc.myo2o.service;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.myo2o.dto.LocalAuthExecution;
import com.imooc.myo2o.entity.LocalAuth;
import com.imooc.myo2o.entity.PersonInfo;
import com.imooc.myo2o.enums.LocalAuthStateEnum;
@RunWith(SpringRunner.class)
@SpringBootTest
public class LocalAuthServiceTest{

	private final String password = "password";
	private final String username = "username";
	@Autowired
	private LocalAuthService localAuthService;
	
	/**
	 * 测试绑定微信账号,service层的测试和dao层的测试的不同就是service会对密码进行加密,所以在数据库中是经过加密的密码
	 */
	@Test
	public void testBindLocalAuth() {
		LocalAuth localAuth = new LocalAuth();
		PersonInfo personInfo = new PersonInfo();

		localAuth.setCreateTime(new Date());
		localAuth.setLastEditTime(new Date());
		localAuth.setPassword(password);
		localAuth.setUserName(username);
		//一个userId(也就是一个personinfo,或者说一个微信账号)只能绑定一个本地账号
		personInfo.setUserId(14L);
		//给平台账号设置用户信息,表明和哪个用户绑定
		localAuth.setPersonInfo(personInfo);
		localAuth.setUserId(14L);
		//进行绑定
		LocalAuthExecution le = localAuthService.bindLocalAuth(localAuth);
		assertEquals(LocalAuthStateEnum.SUCCESS.getStateInfo(), le.getStateInfo());
		System.out.println(le.getStateInfo());
	}
	
	/**
	 * 测试修改密码
	 */
	@Test
	public void testmodifyLocalAuth() {
		String newpassword = "newpassword";
		LocalAuthExecution le = localAuthService.modifyLocalAuth(14L, username, password, newpassword);
		assertEquals(LocalAuthStateEnum.SUCCESS.getStateInfo(), le.getStateInfo());
		System.out.println(le.getStateInfo());
	}

}
