package com.imooc.myo2o.web.frontend;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.myo2o.dto.ShopExecution;
import com.imooc.myo2o.entity.Area;
import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.entity.ShopCategory;
import com.imooc.myo2o.service.AreaService;
import com.imooc.myo2o.service.ShopCategoryService;
import com.imooc.myo2o.service.ShopService;
import com.imooc.myo2o.util.HttpServletRequestUtil;

@Controller
@RequestMapping("/frontend")
public class ShopListController {
	@Autowired
	private AreaService areaService;
	@Autowired
	private ShopCategoryService shopCategoryService;
	@Autowired
	private ShopService shopService;

	/**
	 * 返回商品的一级店铺列表或者二级店铺列表,以及区域信息列表(返回结果根据店铺权重由大到小排序)
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listshopspageinfo", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listShopsPageInfo(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//试着从前端请求中获取parentId
		long parentId = HttpServletRequestUtil.getLong(request, "parentId");
		List<ShopCategory> shopCategoryList = null;
		if (parentId != -1) {
			//如果parentId存在,则取出一级店铺下面的二级店铺列表
			try {
				shopCategoryList = shopCategoryService.getShopCategoryList(parentId);
			} catch (Exception e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
			}
		} else {
			//如果不存在parentId,则取出所有一级shopCategory(这个用来实现用户在首页点击"全部商店")
			try {
				shopCategoryList = shopCategoryService.getFirstLevelShopCategoryList();
			} catch (Exception e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
			}
		}
		modelMap.put("shopCategoryList", shopCategoryList);
		//添加区域信息
		List<Area> areaList = null;
		try {
			areaList = areaService.getAreaList();
			modelMap.put("areaList", areaList);
			modelMap.put("success", true);
			return modelMap;
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
		}
		return modelMap;
	}
	
	/**
	 * 获取特定查询条件下的shop列表(返回结果根据店铺权重由大到小排序)
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listshops", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listShops(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//试着获取页码
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		//试着获取每页显示的数量
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		if ((pageIndex > -1) && (pageSize > -1)) {
			//试着获取一级店铺类别
			long parentId = HttpServletRequestUtil.getLong(request, "parentId");
			//试着获取二级店铺类别
			long shopCategoryId = HttpServletRequestUtil.getLong(request, "shopCategoryId");
			//试着获取区域
			long areaId = HttpServletRequestUtil.getLong(request, "areaId");
			//试着获取商铺名称(模糊查询)
			String shopName = HttpServletRequestUtil.getString(request, "shopName");
			//组合所有获取到的查询条件
			Shop shopCondition = compactShopCondition4Search(parentId, shopCategoryId, areaId, shopName);
			//分页查询,总数查询(返回结果根据店铺权重由大到小排序)
			ShopExecution se = shopService.getShopList(shopCondition, pageIndex, pageSize);
			modelMap.put("shopList", se.getShopList());
			modelMap.put("count", se.getCount());
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty pageSize or pageIndex");
		}

		return modelMap;
	}
	
	/**
	 * 组合查询条件,并将查询条件封装到名为shopCondition的Shop实体类中进行返回.
	 * @param parentId
	 * @param shopCategoryId
	 * @param areaId
	 * @param shopName
	 * @return
	 */
	private Shop compactShopCondition4Search(long parentId, long shopCategoryId, long areaId, String shopName) {
		Shop shopCondition = new Shop();
		//如果一级店铺查询条件不为空,则添加进shopCondition
		if (parentId != -1L) {
			ShopCategory parentCategory = new ShopCategory();
			parentCategory.setShopCategoryId(parentId);
			shopCondition.setParentCategory(parentCategory);
		}
		//如果二级店铺查询条件不为空,则添加进shopCondition
		if (shopCategoryId != -1L) {
			ShopCategory shopCategory = new ShopCategory();
			shopCategory.setShopCategoryId(shopCategoryId);
			shopCondition.setShopCategory(shopCategory);
		}
		//如果区域查询条件不为空,则添加进shopCondition
		if (areaId != -1L) {
			Area area = new Area();
			area.setAreaId(areaId);
			shopCondition.setArea(area);
		}
		//如果模糊查询的商铺名称查询条件不为空,则添加进shopCondition
		if (shopName != null) {
			shopCondition.setShopName(shopName);
		}
		//添加查询条件之可用状态为1表示在前端进行展示的店铺都是审核成功了的
		shopCondition.setEnableStatus(1);
		return shopCondition;
	}
}
