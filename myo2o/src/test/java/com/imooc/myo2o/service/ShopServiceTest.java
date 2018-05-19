package com.imooc.myo2o.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.myo2o.dto.ImageHolder;
import com.imooc.myo2o.dto.ShopExecution;
import com.imooc.myo2o.entity.Area;
import com.imooc.myo2o.entity.Shop;
@RunWith(SpringRunner.class)
@SpringBootTest
public class ShopServiceTest{

	@Autowired
	private ShopService shopService;

	@Test
	public void getShopListTest() {
		Shop shopCondition = new Shop();
		Area area = new Area();
		area.setAreaId(4L);
		shopCondition.setArea(area);
		ShopExecution se = shopService.getShopList(shopCondition, 0, 2);
		System.out.println("每页显示的店铺数量:" + se.getShopList().size());
		System.out.println("符合条件的店铺总数:" + se.getCount());
	}

	@Test
	public void getShopByIdTest() {
		Long shopId = 49L;
		Shop shop = shopService.queryShopByShopId(shopId);
		System.out.println(shop);
	}

	@Test
	public void addShopTest() throws FileNotFoundException {
		Shop shop = new Shop();
		shop.setOwnerId(9L);
		shop.setShopName("addShopTest666");
		File shopImg = new File("C:/Users/hh/Pictures/manyCoder.jpg");
		InputStream shopImgInputStream = new FileInputStream(shopImg);
		String fileName = shopImg.getName();
		//记得把shopImg文件放到src/test/resources/目录下,因为这个程序是在/src/test/java/目录下面运行的.否则报Can't read input file!
		ImageHolder imageHolder = new ImageHolder(shopImgInputStream, fileName);
		ShopExecution shopExecution = shopService.addShop(shop, imageHolder);
		assertEquals(0, shopExecution.getState());
	}

	@Test
	public void modifyShopTest() throws FileNotFoundException {
		Shop shop = new Shop();
		shop.setShopId(48L);
		shop.setOwnerId(9L);
		shop.setShopName("addShopTest666");
		File shopImg = new File("C:/Users/hh/Pictures/FLAMING MOUNTAIN.JPG");
		InputStream shopImgInputStream = new FileInputStream(shopImg);
		String fileName = shopImg.getName();
		ImageHolder imageHolder = new ImageHolder(shopImgInputStream, fileName);
		ShopExecution shopExecution = shopService.modifyShop(shop, imageHolder);
		System.out.println(shopExecution.getStateInfo());
	}
}
