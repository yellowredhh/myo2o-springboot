package com.imooc.myo2o.service;

import static org.junit.Assert.assertEquals;

import java.util.Date;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.myo2o.dto.WechatAuthExecution;
import com.imooc.myo2o.entity.PersonInfo;
import com.imooc.myo2o.entity.WechatAuth;
import com.imooc.myo2o.enums.WechatAuthStateEnum;
@RunWith(SpringRunner.class)
@SpringBootTest
public class WechatAuthServiceTest{

	@Autowired
	private WechatAuthService wechatAuthService;

	@Test
	public void testRegister() {
		WechatAuth wechatAuth = new WechatAuth();
		PersonInfo personInfo = new PersonInfo();

		wechatAuth.setOpenId("dafahizhfdhaih");
		personInfo.setName("test user");
		personInfo.setCreateTime(new Date());
		personInfo.setCustomerFlag(1);
		wechatAuth.setPersonInfo(personInfo);
		WechatAuthExecution we = wechatAuthService.register(wechatAuth, null);
		assertEquals(WechatAuthStateEnum.SUCCESS.getStateInfo(), we.getStateInfo());

		WechatAuth wechatAuth2 = wechatAuthService.getWechatAuthByOpenId("dafahizhfdhaih");
		System.out.println(wechatAuth2.getPersonInfo().getName());
	}

}
