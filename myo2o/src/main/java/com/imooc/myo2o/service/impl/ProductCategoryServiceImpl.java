package com.imooc.myo2o.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.myo2o.Exceptions.ProductCategoryExecutionException;
import com.imooc.myo2o.Exceptions.ProductExecutionException;
import com.imooc.myo2o.dao.ProductCategoryDao;
import com.imooc.myo2o.dao.ProductDao;
import com.imooc.myo2o.dto.ProductCategoryExecution;
import com.imooc.myo2o.entity.ProductCategory;
import com.imooc.myo2o.enums.ProductCategoryStateEnum;
import com.imooc.myo2o.service.ProductCategoryService;

@Service
public class ProductCategoryServiceImpl implements ProductCategoryService {

	@Autowired
	ProductCategoryDao productCategoryDao;
	@Autowired
	ProductDao productDao;

	@Override
	public List<ProductCategory> getProductCategoryList(Long shopId) {
		return productCategoryDao.queryProductCategoryList(shopId);
	}
	
	/*
	 * 批量增加商品类别
	 * (non-Javadoc)
	 * @see com.imooc.myo2o.service.ProductCategoryService#batchAddProductCategory(java.util.List)
	 */
	@Override
	@Transactional
	public ProductCategoryExecution batchAddProductCategory(List<ProductCategory> productCategoryList)
			throws ProductCategoryExecutionException {
		if (productCategoryList != null && productCategoryList.size() != 0) {
			try {
				int effectNumber = productCategoryDao.batchInsertProductCategory(productCategoryList);
				if (effectNumber <= 0) {
					throw new ProductCategoryExecutionException("创建店铺失败");
				} else {
					return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS, productCategoryList);
				}
			} catch (ProductCategoryExecutionException pcee) {
				throw new ProductCategoryExecutionException("batchAddProductCategory error:" + pcee.getMessage());
			}
		} else {
			return new ProductCategoryExecution(ProductCategoryStateEnum.EMPTY_LIST);
		}
	}

	/*
	 * 删除商品类别分两步走:
	 * 1:先把商品表中的含有要删除商品类别的商品的商品类别信息设置为null;
	 * 2:从商品类别表中删除掉这个商品类别条目.
	 * @see com.imooc.myo2o.service.ProductCategoryService#deleteProductCategory(long, long)
	 */
	@Override
	@Transactional
	public ProductCategoryExecution deleteProductCategory(long productCategoryId, long shopId)
			throws ProductCategoryExecutionException {
		//1:将此类别下的商品里的类别id置为空(商品类别都被删除了,那么原有商品中有这个被删除的商品类别的商品里面的商品类别信息也要设置为null)
		try {
			int effectNumber = productDao.updateProductCategoryToNull(productCategoryId);
			if (effectNumber <= 0) {
				throw new ProductCategoryExecutionException("商品类别更新失败");
			}
		} catch (ProductCategoryExecutionException e) {
			throw new ProductExecutionException("updateProductCategoryToNullError:" + e.getMessage());
		}
		//2:在商品类别表中删除掉这个商品类别条目
		try {
			int effectNumber = productCategoryDao.deleteProductCategory(productCategoryId, shopId);
			if (effectNumber <= 0) {
				throw new ProductCategoryExecutionException("删除商品分类失败");
			} else {
				return new ProductCategoryExecution(ProductCategoryStateEnum.SUCCESS);
			}
		} catch (ProductCategoryExecutionException e) {
			throw new ProductCategoryExecutionException("deleteProductCategoryError:" + e.getMessage());
		}
	}
}
