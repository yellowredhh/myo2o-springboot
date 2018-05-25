package com.imooc.myo2o.web.shop;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpUpgradeHandler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.myo2o.dto.ShopAuthMapExecution;
import com.imooc.myo2o.entity.PersonInfo;
import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.entity.ShopAuthMap;
import com.imooc.myo2o.enums.ShopAuthMapStateEnum;
import com.imooc.myo2o.service.ShopAuthMapService;
import com.imooc.myo2o.util.CodeUtil;
import com.imooc.myo2o.util.HttpServletRequestUtil;

@Controller
@RequestMapping("/shop")
public class ShopAuthManagementController {
	@Autowired
	private ShopAuthMapService shopAuthMapService;

	// 列出当前店铺的所有的授权信息
	@RequestMapping(value = "/listshopauthmapsbyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listShopAuthMapsByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 取出分页信息
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		// 从session中获取currentShop
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		// 空值判定
		if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null) && (currentShop.getShopId() != null)) {
			// 分页取出店铺下面的授权信息列表
			ShopAuthMapExecution se = shopAuthMapService.listShopAuthMapByShopId(currentShop.getShopId(), pageIndex,
					pageSize);
			modelMap.put("shopAuthMapList", se.getShopAuthMapList());
			modelMap.put("count", se.getCount());
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
		}
		return modelMap;
	}

	// 根据shopAuthId获取商铺授权列表(主要在编辑某一个店铺下面某一个人的授权信息的时候使用)
	@RequestMapping(value = "/getshopauthmapbyid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopAuthMapById(@RequestParam Long shopAuthId) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 非空判定
		if (shopAuthId != null && shopAuthId > -1) {
			// 根据前台传入的shopAuthId查找对应的授权信息
			ShopAuthMap shopAuthMap = shopAuthMapService.getShopAuthMapById(shopAuthId);
			modelMap.put("shopAuthMap", shopAuthMap);
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty shopAuthId");
		}
		return modelMap;
	}

	/**
	 * 添加商品授权映射信息
	 * 
	 * @param shopAuthMapStr
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/addshopauthmap", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> addShopAuthMap(String shopAuthMapStr, HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		ObjectMapper mapper = new ObjectMapper();
		ShopAuthMap shopAuthMap = null;
		try {
			shopAuthMap = mapper.readValue(shopAuthMapStr, ShopAuthMap.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		if (shopAuthMap != null) {
			try {
				// 从session中获取当前店铺信息和当前登录用户的信息,只有当前登录用户为当前店铺的拥有着时才能进行授权管理操作
				Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
				PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
				// 必须是本店的店主才能进行添加店铺授权信息的操作
				if (currentShop.getOwnerId() != user.getUserId()) {
					modelMap.put("success", false);
					modelMap.put("errMsg", "无操作权限");
					return modelMap;
				}
				shopAuthMap.setShopId(currentShop.getShopId());
				shopAuthMap.setEmployeeId(user.getUserId());
				ShopAuthMapExecution se = shopAuthMapService.addShopAuthMap(shopAuthMap);
				if (se.getState() == ShopAuthMapStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}

		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入授权信息");
		}
		return modelMap;
	}

	/**
	 * 修改商铺授权信息
	 * 
	 * @param shopAuthMapStr
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/modifyshopauthmap", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> modifyShopAuthMap(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 验证码校验
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		ObjectMapper mapper = new ObjectMapper();
		ShopAuthMap shopAuthMap = null;
		String shopAuthMapStr = HttpServletRequestUtil.getString(request, "shopAuthMapStr");
		try {
			// 将前台传入的字符串json转换成shopAuthMap实例
			shopAuthMap = mapper.readValue(shopAuthMapStr, ShopAuthMap.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		// 空值判定
		if (shopAuthMap != null && shopAuthMap.getShopAuthId() != null) {
			try {
				Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
				PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
				shopAuthMap.setShopId(currentShop.getShopId());
				shopAuthMap.setEmployeeId(user.getUserId());
				ShopAuthMapExecution se = shopAuthMapService.modifyShopAuthMap(shopAuthMap);
				if (se.getState() == ShopAuthMapStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}

		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入要修改的授权信息");
		}
		return modelMap;
	}

	/**
	 * 删除授权信息的方法
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/removeshopauthmap", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> removeShopAuthMap(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 从session中获取shopId
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		Long shopId = currentShop.getShopId();
		// 从POST请求体中获取到shopAuthId.
		Long shopAuthMapId = HttpServletRequestUtil.getLong(request, "shopAuthId");
		if (shopAuthMapId > 0) {
			try {
				ShopAuthMapExecution se = shopAuthMapService.removeShopAuthMap(shopAuthMapId, shopId);
				if (se.getState() == ShopAuthMapStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}

		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请至少选择一个授权进行删除");
		}
		return modelMap;
	}
}
