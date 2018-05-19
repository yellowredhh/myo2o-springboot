package com.imooc.myo2o.dao;

import java.util.List;

import com.imooc.myo2o.entity.ProductImg;

public interface ProductImgDao {

	/**
	 * 批量添加商品详情图片
	 * @param productImgList
	 * @return
	 */
	public int batchInsertProductImg(List<ProductImg> productImgList);

	List<ProductImg> queryProductImgList(long productId);

	/**
	 * 删除指定商品下面的商品详情图片
	 * @param productId
	 * @return
	 */
	int deleteProductImgByProductId(long productId);
}
