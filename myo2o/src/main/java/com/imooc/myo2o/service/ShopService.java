package com.imooc.myo2o.service;

import com.imooc.myo2o.dto.ImageHolder;
import com.imooc.myo2o.dto.ShopExecution;
import com.imooc.myo2o.entity.Shop;

public interface ShopService {

	/**
	 * 查询该用户下面的店铺信息
	 * 
	 * @param long
	 *            employyeeId
	 * @return List<Shop>
	 * @throws Exception
	 */
	ShopExecution getByEmployeeId(long employeeId) throws RuntimeException;

	public ShopExecution getShopList(Shop shopCondition, int pageIndex, int pageSize);

	/*
	 * 根据店铺id查询店铺信息
	 */
	public Shop queryShopByShopId(Long shopId);

	/*
	 * 修改店铺
	 */
	public ShopExecution modifyShop(Shop shop, ImageHolder imageHolder);

	/*
	 * 增加商铺
	 */
	public ShopExecution addShop(Shop shop, ImageHolder imageHolder);
}
