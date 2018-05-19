package com.imooc.myo2o.service;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.imooc.myo2o.entity.Area;
@RunWith(SpringRunner.class)
@SpringBootTest
public class AreaServiceTest{
	
	@Autowired
	private AreaService areaService;
	
	@Test
	public void getAreaListTest() throws JsonParseException, JsonMappingException, IOException{
		List<Area> areaList = areaService.getAreaList();
		assertEquals("东苑",areaList.get(0).getAreaName());
		System.out.println(areaList);
	}
}
