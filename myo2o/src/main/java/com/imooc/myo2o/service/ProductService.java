package com.imooc.myo2o.service;

import java.util.List;

import com.imooc.myo2o.Exceptions.ProductExecutionException;
import com.imooc.myo2o.dto.ImageHolder;
import com.imooc.myo2o.dto.ProductExecution;
import com.imooc.myo2o.entity.Product;

public interface ProductService {

	/**
	 * 分页查询获取商品列表   可输入的条件有:商品名(模糊名),商品状态,商品所属店铺id,商品类别id.
	 * @param productCondition
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	public ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize);

	/**
	 * 添加商品信息
	 * @param product	商品信息
	 * @param thumbnail   商品缩略图
	 * @param productImageList   商品详情图片列表
	 * @return
	 */
	public ProductExecution addProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImageList)
			throws ProductExecutionException;

	/**
	 * 根据商品id获取商品信息
	 * @param productId
	 * @return
	 */
	public Product getProductByProductId(long productId);

	/**
	 * 修改商品信息
	 * @param product
	 * @param thumbnail
	 * @param productImageList
	 * @return
	 */
	public ProductExecution modifyProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImageList)
			throws ProductExecutionException;

}
