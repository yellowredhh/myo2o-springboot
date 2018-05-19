package com.imooc.myo2o.cache;

import java.util.ArrayList;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.myo2o.dao.WechatAuthDao;
import com.imooc.myo2o.entity.WechatAuth;

@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class JedisUtilTest {
	@Autowired
	private JedisUtil jedisUtil;
	@Autowired
	private JedisUtil.Strings jedisStrings;
	@Autowired
	private JedisUtil.Sets jedisSets;
	@Autowired
	private JedisUtil.Keys jedisKeys;

	@Autowired
	private WechatAuthDao wechatAuthDao;

	@Test
	public void testSetListAndGetList() throws Exception {
		ObjectMapper mapper = new ObjectMapper();
		WechatAuth wechatAuth = wechatAuthDao.queryWechatInfoByOpenId("dafahizhfdhaih");
		List<WechatAuth> wechatAuthList = new ArrayList<WechatAuth>();
		wechatAuthList.add(wechatAuth);
		//将对象转换为json字符串
		String jsonString = mapper.writeValueAsString(wechatAuthList);
		System.out.println(jsonString);
		//设置到redis缓存中
		jedisStrings.set("shopCategoryList", jsonString);
		//从redis缓存中取出字符串
		String scListString = jedisStrings.get("shopCategoryList");
		System.out.println(scListString);
		//将json字符串转换位java对象
		JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, WechatAuth.class);
		//反序列化读取到的值
		wechatAuthList = mapper.readValue(scListString, javaType);
		for (WechatAuth wa : wechatAuthList) {
			System.out.println(wa.getPersonInfo().getName());
		}
	}

}
