package com.imooc.myo2o.service;

import java.util.List;

import com.imooc.myo2o.Exceptions.ProductCategoryExecutionException;
import com.imooc.myo2o.dto.ProductCategoryExecution;
import com.imooc.myo2o.entity.ProductCategory;

public interface ProductCategoryService {

	/*
	 * 根据传入的shopId查询商品列表
	 */
	public List<ProductCategory> getProductCategoryList(Long shopId);

	/*
	 * 批量增加商品类别.
	 */
	public ProductCategoryExecution batchAddProductCategory(List<ProductCategory> productCategoryList)
			throws ProductCategoryExecutionException;

	/* 将此类别下的商品里的类别id置为空,再删除该商品类别
	 * 删除某一个商品分类(多传入一个shopId作为验证)
	 */
	public ProductCategoryExecution deleteProductCategory(long productCategoryId, long shopId)
			throws ProductCategoryExecutionException;
}
