package com.imooc.myo2o.service;

import javax.servlet.http.HttpServletRequest;

import com.imooc.myo2o.dto.ShopAuthMapExecution;
import com.imooc.myo2o.entity.ShopAuthMap;

public interface ShopAuthMapService {
	/**
	 * 根据店铺id查询店铺授权信息
	 * 
	 * @param shopId
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	ShopAuthMapExecution listShopAuthMapByShopId(Long shopId, Integer pageIndex, Integer pageSize);

	/**
	 * 根据shopAuthId返回对应的授权信息
	 * 
	 * @param shopAuthId
	 * @return
	 */
	ShopAuthMap getShopAuthMapById(Long shopAuthId);

	/**
	 * 添加店铺授权信息
	 * 
	 * @param shopAuthMap
	 * @return
	 * @throws RuntimeException
	 */
	ShopAuthMapExecution addShopAuthMap(ShopAuthMap shopAuthMap) throws RuntimeException;

	/**
	 * 更新授权信息，包括职位等
	 * 
	 * @param shopAuthId
	 * @param title
	 * @param titleFlag
	 * @param enableStatus
	 * @return
	 * @throws RuntimeException
	 */
	ShopAuthMapExecution modifyShopAuthMap(ShopAuthMap shopAuthMap) throws RuntimeException;

	/**
	 * 移除店铺授权信息
	 * 
	 * @param shopAuthMapId
	 * @param request
	 * @return
	 * @throws RuntimeException
	 */
	ShopAuthMapExecution removeShopAuthMap(Long shopAuthMapId, Long shopId) throws RuntimeException;

}
