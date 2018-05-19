package com.imooc.myo2o.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.FixMethodOrder;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.imooc.myo2o.entity.Product;
import com.imooc.myo2o.entity.ProductCategory;
import com.imooc.myo2o.entity.ProductImg;
import com.imooc.myo2o.entity.Shop;
@RunWith(SpringRunner.class)
@SpringBootTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ProductDaoTest{

	@Autowired
	private ProductDao productDao;
	@Autowired
	private ProductImgDao productImgDao;

	@Test
	public void testAInsertProduct() throws Exception {
		Shop shop = new Shop();
		shop.setShopId(20L);
		ProductCategory pc1 = new ProductCategory();
		pc1.setProductCategoryId(9L);
		ProductCategory pc2 = new ProductCategory();
		pc2.setProductCategoryId(10L);
		ProductCategory pc3 = new ProductCategory();
		pc3.setProductCategoryId(11L);
		
		Product product1 = new Product();
		product1.setProductName("测试案例1");
		product1.setProductDesc("测试Desc1");
		product1.setImgAddr("test1");
		product1.setPriority(0);
		product1.setEnableStatus(1);
		product1.setCreateTime(new Date());
		product1.setLastEditTime(new Date());
		product1.setShop(shop);
		product1.setProductCategory(pc1);
		
		Product product2 = new Product();
		product2.setProductName("测试案例2");
		product2.setProductDesc("测试Desc2");
		product2.setImgAddr("test2");
		product2.setPriority(0);
		product2.setEnableStatus(0);
		product2.setCreateTime(new Date());
		product2.setLastEditTime(new Date());
		product2.setShop(shop);
		product2.setProductCategory(pc2);
		
		Product product3 = new Product();
		product3.setProductName("测试案例3");
		product3.setProductDesc("测试Desc3");
		product3.setImgAddr("test3");
		product3.setPriority(0);
		product3.setEnableStatus(1);
		product3.setCreateTime(new Date());
		product3.setLastEditTime(new Date());
		product3.setShop(shop);
		product3.setProductCategory(pc3);
		
		int effectedNum = productDao.insertProduct(product1);
		assertEquals(1, effectedNum);
		effectedNum = productDao.insertProduct(product2);
		assertEquals(1, effectedNum);
		effectedNum = productDao.insertProduct(product3);
		assertEquals(1, effectedNum);
	}

	/*
	 * 分页查询,预期返回3条结果
	 */
	@Test
	public void testBQueryProductList() throws Exception {
		Product product = new Product();
		product.setProductName("测试案例");
		List<Product> productList = productDao.queryProductList(product, 0, 2);
		//单页数量预期为2
		assertEquals(2, productList.size());
		int count = productDao.queryProductCount(product);
		//分页查询总数预期为3
		assertEquals(3, count);
	}

	@Test
	public void testCQueryProductByProductId() throws Exception {
		long productId = 36;
		ProductImg productImg1 = new ProductImg();
		productImg1.setImgAddr("图片1");
		productImg1.setImgDesc("测试图片1");
		productImg1.setPriority(1);
		productImg1.setCreateTime(new Date());
		productImg1.setProductId(productId);
		ProductImg productImg2 = new ProductImg();
		productImg2.setImgAddr("图片2");
		productImg1.setImgDesc("测试图片2");
		productImg2.setPriority(1);
		productImg2.setCreateTime(new Date());
		productImg2.setProductId(productId);
		List<ProductImg> productImgList = new ArrayList<ProductImg>();
		productImgList.add(productImg1);
		productImgList.add(productImg2);
		int effectedNum = productImgDao.batchInsertProductImg(productImgList);
		assertEquals(2, effectedNum);
		
		Product product = productDao.queryProductByProductId(productId);
		assertEquals(2, product.getProductImgList().size());
		
		effectedNum = productImgDao.deleteProductImgByProductId(productId);
		assertEquals(2, effectedNum);
	}

	@Test
	public void testDUpdateProduct() throws Exception {
		Product product = new Product();
		product.setProductId(36L);
		product.setProductName("用于测试22222,请不要手动删除");
		product.setLastEditTime(new Date());
		Shop shop = new Shop();
		shop.setShopId(20L);
		product.setShop(shop);
		int effectedNum = productDao.updateProduct(product);
		assertEquals(1, effectedNum);
	}
	
	
	//将某一商品类别商品的商品类别置为null
	@Test 
	public void testupdateProductCategoryToNull(){
//		UPDATE tb_product
//		SET
//		product_category_id = null
//		WHERE product_category_id =
//		#{productCategoryId}
		int effectNumber = productDao.updateProductCategoryToNull(91L);
		assertEquals(1,effectNumber);
	}
	
	@Ignore
	@Test
	public void testEDeleteShopAuthMap() throws Exception {
		int effectedNum = productDao.deleteProduct(2, 1);
		assertEquals(1, effectedNum);
	}
}
