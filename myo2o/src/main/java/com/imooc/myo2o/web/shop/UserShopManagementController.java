package com.imooc.myo2o.web.shop;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.myo2o.dto.UserShopMapExecution;
import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.entity.UserShopMap;
import com.imooc.myo2o.service.UserShopMapService;
import com.imooc.myo2o.util.HttpServletRequestUtil;

@Controller
@RequestMapping("/shop")
public class UserShopManagementController {
	@Autowired
	private UserShopMapService userShopMapService;

	@RequestMapping(value = "/listusershopmapsbyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listUserShopMapsByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 从url中获取分页信息
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		// 从session中获取当前店铺
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		// 空值判定
		if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null) && (currentShop.getShopId() != null)) {
			// 传入查询条件
			UserShopMap userShopMapCondition = new UserShopMap();
			userShopMapCondition.setShopId(currentShop.getShopId());
			String userName = HttpServletRequestUtil.getString(request, "userName");
			if (userName != null) {
				// 若传入了顾客姓名,则按照顾客姓名模糊查询
				userShopMapCondition.setUserName(userName);
			}
			// 分页获取该店铺下的顾客积分列表
			UserShopMapExecution ue = userShopMapService.listUserShopMap(userShopMapCondition, pageIndex, pageSize);
			modelMap.put("userShopMapList", ue.getUserShopMapList());
			modelMap.put("count", ue.getCount());
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
		}
		return modelMap;
	}

}
