package com.imooc.myo2o.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.imooc.myo2o.Exceptions.ShopExecutionException;
import com.imooc.myo2o.dao.ShopDao;
import com.imooc.myo2o.dto.ImageHolder;
import com.imooc.myo2o.dto.ShopExecution;
import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.enums.ShopStateEnums;
import com.imooc.myo2o.service.ShopService;
import com.imooc.myo2o.util.ImageUtils;
import com.imooc.myo2o.util.PageCalculator;
import com.imooc.myo2o.util.PathUtil;

@Service
public class ShopServiceImpl implements ShopService {

	@Autowired
	private ShopDao shopDao;

	@Override
	public ShopExecution getByEmployeeId(long employeeId) throws RuntimeException {
		List<Shop> shopList = shopDao.queryByEmployeeId(employeeId);
		ShopExecution se = new ShopExecution();
		se.setShopList(shopList);
		return se;
	}

	@Override
	@Transactional
	/*这个方法完成了添加店铺并给店铺添加缩略图的功能,先添加店铺信息,然后添加缩略图的相对路径属性,然后更新店铺信息(把添加的缩略图路径属性刷新进去)
	 * (non-Javadoc)
	 * @see com.imooc.myo2o.service.ShopService#addShop(com.imooc.myo2o.entity.Shop, java.io.File)
	 * @return 	ShopExecution是service层的处理结果,里面包含了处理状态和shop等信息
	 */
	public ShopExecution addShop(Shop shop, ImageHolder imageHolder) {
		if (shop == null) {
			//throw new ShopExecutionException(ShopStateEnums.NULL_SHOP_INFO.getStateInfo());
			return new ShopExecution(ShopStateEnums.NULL_SHOP_INFO);
		} else {
			try {
				shop.setEnableStatus(ShopStateEnums.CHECK.getState());
				shop.setCreateTime(new Date());
				shop.setLastEditTime(new Date());
				//添加店铺信息
				int effectNumber = shopDao.insertShop(shop);
				if (effectNumber <= 0) {
					throw new ShopExecutionException("添加店铺信息失败");
				} else {
					if (imageHolder.getImageInputStream() != null) {
						try {
							/*
							 * 在这里更改了shop对象的属性,由于java对于形参不论是基本数据类型还是对象类型都是采用的值传递
							 *基本数据类型:直接传递数据的拷贝
							 *对象类型:传递的是该对象所指向的堆对象的指针的拷贝,也就是两者会指向同一个堆对象,所以这里改变了shop的属性,在方法外面的shop属性也会改变.
							 */
							//给shop实例添加店铺图片的相对路径属性
							addShopImg(shop, imageHolder);
						} catch (Exception e) {
							throw new ShopExecutionException("添加店铺缩略图失败:" + e.getMessage());
						}
						effectNumber = shopDao.updateShop(shop);
						if (effectNumber <= 0) {
							throw new ShopExecutionException("更新图片地址失败");
						}
					}
				}
			} catch (Exception e) {
				throw new ShopExecutionException("addShopError:" + e.getMessage());
			}
		}
		//添加成功,返回商铺在"审核中"的状态标识以及商品信息.
		return new ShopExecution(ShopStateEnums.CHECK, shop);
	}

	//首先通过shopId获取到商铺的
	private void addShopImg(Shop shop, ImageHolder imageHolder) {
		//通过shopId来获取商铺的图片的相对路径(这个方法里面有一个硬编码加入了相对路径)
		String shopImagePath = PathUtil.getShopImagePath(shop.getShopId());
		//生成缩略图,并生成新的商品缩略图相对路径
		String shopImgAddr = ImageUtils.generateThumbnail(imageHolder, shopImagePath);
		//更新商品缩略图的相对路径
		shop.setShopImg(shopImgAddr);
	}

	@Override
	public Shop queryShopByShopId(Long shopId) {
		Shop shop = shopDao.queryShopByShopId(shopId);
		return shop;
	}

	/*
	 * 对商铺信息进行更新操作
	 */
	@Override
	@Transactional
	public ShopExecution modifyShop(Shop shop, ImageHolder imageHolder) throws ShopExecutionException {
		if (shop == null || shop.getShopId() == null) {
			return new ShopExecution(ShopStateEnums.NULL_SHOP_INFO);
		} else {
			try {
				//1.判断是否要对商铺的图片进行修改(这里采用的策略是如果要对图片进行更改就删除原来的图片,其实可以不用删除,直接添加新图片,在数据库中会记录新的图片地址.原来的图片还可以留在服务器中,方便以后添加功能.比如历史头像等)
				if (imageHolder.getImageInputStream() != null && imageHolder.getImageName() != null
						&& !" ".equals(imageHolder.getImageName())) {
					//这里创建一个tempShop的好处是.如果直接拿传入的shop参数去获取原图片地址信息,用户可能对图片地址做了改动,比如用户不想要图片了,设置成了空的
					Shop tempShop = shopDao.queryShopByShopId(shop.getShopId());
					//根据传入的shop拿到原shop的图片地址,进行删除图片操作
					if (tempShop.getShopImg() != null) {
						ImageUtils.deleteFileOrPath(tempShop.getShopImg());
					}
					addShopImg(shop, imageHolder);
				}
				//2.更新商铺信息
				shop.setLastEditTime(new Date());
				int effectNumber = shopDao.updateShop(shop);
				if (effectNumber <= 0) {
					return new ShopExecution(ShopStateEnums.INNER_ERROR);
				} else {
					//拿到更新过后的shop.
					shop = shopDao.queryShopByShopId(shop.getShopId());
					return new ShopExecution(ShopStateEnums.SUCCESS, shop);
				}
			} catch (Exception e) {
				throw new ShopExecutionException("shopModifyError:" + e.getMessage());
			}
		}
	}

	@Override
	public ShopExecution getShopList(Shop shopCondition, int pageIndex, int pageSize) {
		//行页转换
		int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
		//分页查询出符合要求的店铺列表(返回结果根据店铺权重由大到小排序)
		List<Shop> shopList = shopDao.queryShopList(shopCondition, rowIndex, pageSize);
		//在同样的条件下查询店铺总数
		int shopCount = shopDao.queryShopCount(shopCondition);
		ShopExecution se = new ShopExecution();
		if (shopList != null) {
			se.setShopList(shopList);
			se.setCount(shopCount);
		} else {
			se.setState(ShopStateEnums.INNER_ERROR.getState());
		}
		return se;
	}
}
