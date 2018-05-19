package com.imooc.myo2o.interceptor.shop;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.imooc.myo2o.entity.Shop;

/**
 * 店家管理系统操作验证拦截器
 * @author hh
 *
 */
public class ShopPermissionInterceptor extends HandlerInterceptorAdapter {
	/**
	 * 主要做事前拦截,即用户操作发生前,改写preHandle里的逻辑,进行拦截.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//从session中获取当前选择的店铺(这个currentShop在ShopManagementController类中的getShopManagementInfo方法中赋值到session中去)
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		@SuppressWarnings("unchecked")
		//从session中获取当前用户可操作的店铺列表(这个shopList在ShopManagementController类中的getShopList方法中被放到session中)
		List<Shop> shopList = (List<Shop>) request.getSession().getAttribute("shopList");
		if (currentShop != null && shopList != null) {
			//遍历可操作的店铺列表
			for (Shop shop : shopList) {
				//如果当前店铺在可操作的店铺列表中,则返回true,进行接下来的用户操作
				if (shop.getShopId() == currentShop.getShopId()) {
					return true;
				}
			}
		}
		//如果没有权限则终止用户继续操作
		return false;
	}
}
