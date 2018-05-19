package com.imooc.myo2o.service;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.myo2o.dto.ImageHolder;
import com.imooc.myo2o.dto.ProductExecution;
import com.imooc.myo2o.entity.Product;
import com.imooc.myo2o.entity.ProductCategory;
import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.enums.ProductStateEnum;
@RunWith(SpringRunner.class)
@SpringBootTest
public class ProductServiceTest{
	@Autowired
	private ProductService productService;

	@Test
	public void AaddProductTest() throws FileNotFoundException {
		Logger logger = LoggerFactory.getLogger(ProductServiceTest.class);
		//给商品添加商品类别id和商铺id
		Product product = new Product();
		Shop shop = new Shop();
		shop.setShopId(15L);
		ProductCategory productCategory = new ProductCategory();
		productCategory.setProductCategoryId(10L);
		product.setShop(shop);
		product.setProductCategory(productCategory);
		//添加其它信息
		//		product.setCreateTime(new Date());
		//		product.setLastEditTime(new Date());
		//		product.setEnableStatus(1);
		product.setPriority(20);
		product.setProductDesc("测试商品添加1");
		product.setProductName("addProductTest");

		//添加缩略图
		File thumbnailsImage = new File("C:/Users/hh/Pictures/manyCoder.jpg");
		InputStream thumbnailsImageInputStream = new FileInputStream(thumbnailsImage);
		ImageHolder thumbnails = new ImageHolder(thumbnailsImageInputStream, thumbnailsImage.getName());

		//添加两张商品详情图
		List<ImageHolder> productImageList = new ArrayList<ImageHolder>();
		File productImg1 = new File("C:/Users/hh/Pictures/google.jpg");
		logger.debug("productImg1相关信息:是否是文件:" + productImg1.isFile() + "---" + productImg1.getPath());

		InputStream productImgInputStream1 = new FileInputStream(productImg1);
		ImageHolder productImgHolder1 = new ImageHolder(productImgInputStream1, productImg1.getName());
		productImageList.add(productImgHolder1);
		File productImg2 = new File("C:/Users/hh/Pictures/googleBug.jpg");
		logger.debug("productImg2相关信息:是否是文件:" + productImg2.isFile() + "---" + productImg2.getPath());

		InputStream productImgInputStream2 = new FileInputStream(productImg2);
		ImageHolder productImgHolder2 = new ImageHolder(productImgInputStream2, productImg2.getName());
		productImageList.add(productImgHolder2);

		logger.debug("三个参数是否都有值:" + product + "," + thumbnails + "," + productImageList);
		System.out.println(productService);

		ProductExecution pe = productService.addProduct(product, thumbnails, productImageList);
		assertEquals(ProductStateEnum.SUCCESS.getStateInfo(), pe.getStateInfo());
	}
	
	@Test
	public void modifyProductTest() throws FileNotFoundException {
		Logger logger = LoggerFactory.getLogger(ProductServiceTest.class);
		//36号product是我专门用于测试的.
		Product product = productService.getProductByProductId(36L);
		product.setProductName("modifyProductTest");
		//添加缩略图
		File thumbnailsImage = new File("C:/Users/hh/Pictures/manyCoder.jpg");
		InputStream thumbnailsImageInputStream = new FileInputStream(thumbnailsImage);
		ImageHolder thumbnails = new ImageHolder(thumbnailsImageInputStream, thumbnailsImage.getName());

		//添加两张商品详情图
		List<ImageHolder> productImageList = new ArrayList<ImageHolder>();
		File productImg1 = new File("C:/Users/hh/Pictures/google.jpg");
		logger.debug("productImg1相关信息:是否是文件:" + productImg1.isFile() + "---" + productImg1.getPath());

		InputStream productImgInputStream1 = new FileInputStream(productImg1);
		ImageHolder productImgHolder1 = new ImageHolder(productImgInputStream1, productImg1.getName());
		productImageList.add(productImgHolder1);
		File productImg2 = new File("C:/Users/hh/Pictures/googleBug.jpg");
		logger.debug("productImg2相关信息:是否是文件:" + productImg2.isFile() + "---" + productImg2.getPath());

		InputStream productImgInputStream2 = new FileInputStream(productImg2);
		ImageHolder productImgHolder2 = new ImageHolder(productImgInputStream2, productImg2.getName());
		productImageList.add(productImgHolder2);

		logger.debug("三个参数是否都有值:" + product + "," + thumbnails + "," + productImageList);
		ProductExecution pe = productService.modifyProduct(product, thumbnails, productImageList);
		assertEquals(ProductStateEnum.SUCCESS.getStateInfo(), pe.getStateInfo());
	}
}
