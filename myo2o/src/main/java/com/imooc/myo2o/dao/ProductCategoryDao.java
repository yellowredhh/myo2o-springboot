package com.imooc.myo2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.myo2o.entity.ProductCategory;

public interface ProductCategoryDao {

	/*
	 * 获取一个商铺的所有的商品.
	 */
	public List<ProductCategory> queryProductCategoryList(Long shopId);

	/*
	 * 给店铺增加商品类型(批量增加)
	 */
	public int batchInsertProductCategory(List<ProductCategory> productCategoryList);

	/* 删除某一个店铺的某一个商品分类(其实在数据库ProductCategory表中productCategoryId已经是唯一的了,但是为了在前端删除的时候能够安全一点,所以还是要求传入一个shopId,相当于安全机制吧)
	 * 两个参数mybatis无法区分,所以要加上@Param进行区分.
	 * @return effectNumber
	 */
	public int deleteProductCategory(@Param("productCategoryId") Long productCategoryId, @Param("shopId") Long shopId);

}
