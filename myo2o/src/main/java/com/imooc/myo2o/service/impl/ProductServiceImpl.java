package com.imooc.myo2o.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.myo2o.Exceptions.ProductExecutionException;
import com.imooc.myo2o.dao.ProductDao;
import com.imooc.myo2o.dao.ProductImgDao;
import com.imooc.myo2o.dto.ImageHolder;
import com.imooc.myo2o.dto.ProductExecution;
import com.imooc.myo2o.entity.Product;
import com.imooc.myo2o.entity.ProductImg;
import com.imooc.myo2o.enums.ProductStateEnum;
import com.imooc.myo2o.service.ProductService;
import com.imooc.myo2o.util.ImageUtils;
import com.imooc.myo2o.util.PageCalculator;
import com.imooc.myo2o.util.PathUtil;

@Service
public class ProductServiceImpl implements ProductService {
	@Autowired
	private ProductDao productDao;
	@Autowired
	private ProductImgDao productImgDao;
	
	
	@Override
	public ProductExecution getProductList(Product productCondition, int pageIndex, int pageSize) {
		//行页转换,调用dao取回指定页码的商品.
		int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
		List<Product> productList = productDao.queryProductList(productCondition, rowIndex, pageSize);
		//基于同样的查询条件下的商品数量
		int count = productDao.queryProductCount(productCondition);
		ProductExecution pe = new ProductExecution();
		pe.setProductList(productList);
		pe.setCount(count);
		return pe;
	}

	/*
	 * 1:如果有商品的缩略图,则进行添加
	 * 2:把商品信息添加进tb_product表中.
	 * 3:如果存在商品详情图片,则进行添加
	 * (non-Javadoc)
	 * @see com.imooc.myo2o.service.ProductService#addProduct(com.imooc.myo2o.entity.Product, com.imooc.myo2o.dto.ImageHolder, java.util.List)
	 */
	@Override
	@Transactional
	public ProductExecution addProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImageList)
			throws ProductExecutionException {
		if (product != null && product.getShop() != null && product.getShop().getShopId() != null) {
			product.setCreateTime(new Date());
			product.setLastEditTime(new Date());
			//默认设置商品为上架状态
			product.setEnableStatus(1);
			//1:若缩略图不为空,则添加
			if (thumbnail != null) {
				addThumbnail(product, thumbnail);
			}
			try {
				//2:添加商品信息到tb_product表中.
				int effectedNum = productDao.insertProduct(product);
				if (effectedNum <= 0) {
					throw new ProductExecutionException("创建商品失败");
				}
			} catch (Exception e) {
				throw new ProductExecutionException("创建商品失败:" + e.toString());
			}
			//3:如果存在商品详情图片,则进行添加
			if (productImageList != null && productImageList.size() > 0) {
				addProductImgs(product, productImageList);
			}
			return new ProductExecution(ProductStateEnum.SUCCESS, product);
		} else {
			return new ProductExecution(ProductStateEnum.EMPTY);
		}
	}

	private void addProductImgs(Product product, List<ImageHolder> productImageHolderList) {
		String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
		//如果确实有商品详情图片要添加则进行添加操作
		if (productImageHolderList != null && productImageHolderList.size() > 0) {
			List<ProductImg> productImgList = new ArrayList<ProductImg>();
			//遍历商品详情图片列表,添加每一个商品详情图片的相对地址到商品中.
			for (ImageHolder imageHolder : productImageHolderList) {
				String imgAddr = ImageUtils.generateNormalImg(imageHolder, dest);
				ProductImg productImg = new ProductImg();
				//添加详情图片地址
				productImg.setImgAddr(imgAddr);
				productImg.setProductId(product.getProductId());
				productImg.setCreateTime(new Date());
				productImgList.add(productImg);
			}
			try {
				//批量添加商品详情图片
				int effectedNum = productImgDao.batchInsertProductImg(productImgList);
				if (effectedNum <= 0) {
					throw new RuntimeException("创建商品详情图片失败");
				}
			} catch (Exception e) {
				throw new RuntimeException("创建商品详情图片失败:" + e.toString());
			}
		}
	}

	/*
	 * 1:删除商品的所有详情图
	 * 2:将tb_product_img表中的原有的商品详情图片记录全部删除;
	 */
	private void deleteProductImgs(long productId) {
		//先根据productId获取到商品的详情图列表
		List<ProductImg> productImgList = productImgDao.queryProductImgList(productId);
		//逐个删除详情图片(这个方法只负责删除本地文件夹中的图片,不会删除数据库中的图片地址记录)
		for (ProductImg productImg : productImgList) {
			ImageUtils.deleteFileOrPath(productImg.getImgAddr());
		}
		//删除product_img表中的商品详情图片记录
		productImgDao.deleteProductImgByProductId(productId);
	}

	//给商品添加缩略图
	private void addThumbnail(Product product, ImageHolder thumbnail) {
		String dest = PathUtil.getShopImagePath(product.getShop().getShopId());
		String thumbnailAddr = ImageUtils.generateThumbnail(thumbnail, dest);
		product.setImgAddr(thumbnailAddr);
	}

	/*
	 * 根据productId获取product信息
	 * (non-Javadoc)
	 * @see com.imooc.myo2o.service.ProductService#getProductByProductId(long)
	 */
	@Override
	public Product getProductByProductId(long productId) {
		Product product = productDao.queryProductByProductId(productId);
		return product;
	}

	/*
	 * 修改商品步骤:
	 * 1:若更改商品要更换缩略图,则先删除原缩略图,然后添加新的缩略图;
	 * 2:若商品详情图也要更换,也进行如上操作,并且将tb_product_img表中的原有的商品详情图片记录全部删除;
	 * 3:更新tb_product的信息.
	 * @see com.imooc.myo2o.service.ProductService#modifyProduct(com.imooc.myo2o.entity.Product, com.imooc.myo2o.dto.ImageHolder, java.util.List)
	 */
	@Override
	@Transactional
	public ProductExecution modifyProduct(Product product, ImageHolder thumbnail, List<ImageHolder> productImageList)
			throws ProductExecutionException {
		if (product != null && product.getShop() != null && product.getShop().getShopId() != null) {
			product.setLastEditTime(new Date());
			//1:若缩略图不为空,则先删除原有的缩略图,然后添加新的缩略图
			if (thumbnail != null) {
				//当前传入的product里面的信息是要更新的信息,所以不能用当前的product来删除原有的图片地址.而应当先从后台查询出原有的product信息.
				Product tempProduct = productDao.queryProductByProductId(product.getProductId());
				ImageUtils.deleteFileOrPath(tempProduct.getImgAddr());
				addThumbnail(product, thumbnail);
			}
			//2:如果存在商品详情图片,则先删除原有的商品详情图,并将tb_product_img表中的原有的商品详情图片记录全部删除,然后添加新的商品详情图
			if (productImageList != null && productImageList.size() > 0) {
				deleteProductImgs(product.getProductId());
				//添加新的商品详情图
				addProductImgs(product, productImageList);
			}

			try {
				//3:更新商品信息到tb_product表中.
				int effectedNum = productDao.updateProduct(product);
				if (effectedNum <= 0) {
					throw new ProductExecutionException("更新商品失败");
				}
			} catch (Exception e) {
				throw new ProductExecutionException("更新商品失败:" + e.toString());
			}
			return new ProductExecution(ProductStateEnum.SUCCESS, product);
		} else {
			return new ProductExecution(ProductStateEnum.EMPTY);
		}
	}
}
