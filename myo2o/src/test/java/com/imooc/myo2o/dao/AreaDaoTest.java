package com.imooc.myo2o.dao;


import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.myo2o.entity.Area;
@RunWith(SpringRunner.class)
@SpringBootTest
public class AreaDaoTest{
	
	@Autowired
	private AreaDao areadao;
	
	@Test
	public void queryAreaDao(){
		List<Area> arealist = areadao.queryArea();
		assertEquals(4,arealist.size());
		System.out.println(arealist);
	}
}
