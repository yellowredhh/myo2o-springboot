package com.imooc.myo2o.web.shop;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.util.HttpServletRequestUtil;

/**
 * "@ResponseBody"这个注解表示该方法的返回结果直接写入HTTP response body中，一般在异步获取数据时使用。
 * 在使用@RequestMapping后，返回值通常解析为跳转路径。加上@responsebody后， 返回结果直接写入HTTP response
 * body中，不会被解析为跳转路径。比如异步请求， 希望响应的结果是json数据，那么加上@responsebody后，就会直接返回json数据。
 * 
 * @author hh
 */
@Controller
@RequestMapping(value = "shop", method = { RequestMethod.GET, RequestMethod.POST })
public class AdminController {
	@RequestMapping(value = "/test")
	public Map<String, Object> productcategory(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		String kaptchaExpected = (String) request.getSession()
				.getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
		System.out.println(kaptchaExpected);
		modelMap.put("verifyCode", kaptchaExpected);
		return modelMap;
	}

	@RequestMapping(value = "/ownerlogin")
	public String ownerLogin(HttpServletRequest request) {
		return "shop/ownerlogin";
	}

	@RequestMapping(value = "/register", method = RequestMethod.GET)
	private String register() {
		return "shop/register";
	}

	@RequestMapping(value = "/changepsw", method = RequestMethod.GET)
	private String changePsw() {
		return "shop/changepsw";
	}

	@RequestMapping(value = "/ownerbind", method = RequestMethod.GET)
	private String ownerBind() {
		return "shop/ownerbind";
	}

	@RequestMapping(value = "/shoplist", method = RequestMethod.GET)
	private String myList() {
		return "shop/shoplist";
	}

	/**
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/shopmanage", method = RequestMethod.GET)
	private String shopManage(HttpServletRequest request) {

		long shopId = HttpServletRequestUtil.getLong(request, "shopId");
		if (shopId <= 0) { // 如果没有shopId则去session的currentShop中获取
			Object currentShopObj = request.getSession().getAttribute("currentShop");
			if (currentShopObj == null) {// 如果currentShop也不存在,则跳转到shopList页面去重新选择店铺
				return "shop/shoplist";
			} else {// 如果存在currentShop则进入当前店铺的管理
				return "shop/shopmanage";
			}
		} else {// 如果携带了shopId,则把当前shopId存入到currentShop中,并将currentShop设置到session中去
			Shop currentShop = new Shop();
			currentShop.setShopId(shopId);
			request.getSession().setAttribute("currentShop", currentShop);
			return "shop/shopmanage";
		}

	}

	@RequestMapping(value = "/shopedit", method = RequestMethod.GET)
	private String shopEdit() {
		return "shop/shopedit";
	}

	@RequestMapping(value = "/productmanage", method = RequestMethod.GET)
	private String productManage() {
		return "shop/productmanage";
	}

	@RequestMapping(value = "/productedit", method = RequestMethod.GET)
	private String productEdit() {
		return "shop/productedit";
	}

	@RequestMapping(value = "/productcategorymanage", method = RequestMethod.GET)
	private String productCategoryManage() {
		return "shop/productcategorymanage";
	}

	@RequestMapping(value = "/shopauthmanage", method = RequestMethod.GET)
	private String shopAuthManage() {
		return "shop/shopauthmanage";
	}

	@RequestMapping(value = "/shopauthedit", method = RequestMethod.GET)
	private String shopAuthEdit() {
		return "shop/shopauthedit";
	}

	@RequestMapping(value = "/productbuycheck", method = RequestMethod.GET)
	private String productBuyCheck() {
		return "shop/productbuycheck";
	}

	@RequestMapping(value = "/awarddelivercheck", method = RequestMethod.GET)
	private String awardDeliverCheck() {
		return "shop/awarddelivercheck";
	}

	@RequestMapping(value = "/usershopcheck", method = RequestMethod.GET)
	private String userShopCheck() {
		return "shop/usershopcheck";
	}

	@RequestMapping(value = "/awardmanage", method = RequestMethod.GET)
	private String awardManage() {
		return "shop/awardmanage";
	}

	@RequestMapping(value = "/awardedit", method = RequestMethod.GET)
	private String awardEdit() {
		return "shop/awardedit";
	}

	@RequestMapping(value = "/customermanage", method = RequestMethod.GET)
	private String customerManage() {
		return "shop/customermanage";
	}
}
