package com.imooc.myo2o.web.shopadmin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.myo2o.Exceptions.ProductCategoryExecutionException;
import com.imooc.myo2o.dto.ProductCategoryExecution;
import com.imooc.myo2o.dto.Result;
import com.imooc.myo2o.entity.ProductCategory;
import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.enums.ProductCategoryStateEnum;
import com.imooc.myo2o.service.ProductCategoryService;

@Controller
@RequestMapping("/shopadmin")
public class ProductCategoryManagementController {

	@Autowired
	ProductCategoryService productCategoryService;

	
	@RequestMapping(value = "/getproductcategorylist", method = RequestMethod.GET)
	@ResponseBody
	public Map<String, Object> getProductCategoryList(HttpServletRequest request) {
		//To be removed
		//		Shop shop = new Shop();
		//		shop.setShopId(20L);
		//		request.getSession().setAttribute("currentShop", shop);
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		List<ProductCategory> productCategoryList = null;
		if (currentShop != null && currentShop.getShopId() > 0) {
			productCategoryList = productCategoryService.getProductCategoryList(currentShop.getShopId());
			//return new Result<List<ProductCategory>>(true, productCategoryList);
			//如果你要用Result这个类来包装结果,记得在前端页面获取后台返回结果的时候也要改名称.(在Result这个类中是用data属性表示数据)
			modelMap.put("success", true);
			modelMap.put("productCategoryList", productCategoryList);
			return modelMap;
		} else {
			ProductCategoryStateEnum ps = ProductCategoryStateEnum.INNER_ERROR;
			//return new Result<List<ProductCategory>>(false, ps.getState(), ps.getStateInfo());
			modelMap.put("success", false);
			modelMap.put("errMsg", ps.getStateInfo());
			return modelMap;
		}
	}

	@RequestMapping(value = "/addproductcategorys", method = RequestMethod.POST)
	@ResponseBody
	//从前端传过来的是一个集合,所以要用到RequestBody.
	public Map<String, Object> addProductCategorys(@RequestBody List<ProductCategory> productCategoryList,
			HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		//取出session中的ShopId设置到集合的每一个商品分类中去.
		for (ProductCategory productCategory : productCategoryList) {
			productCategory.setShopId(currentShop.getShopId());
		}
		if (productCategoryList != null && productCategoryList.size() > 0) {
			try {
				ProductCategoryExecution pce = productCategoryService.batchAddProductCategory(productCategoryList);
				if (pce.getState() == ProductCategoryStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", pce.getStateInfo());
				}
			} catch (ProductCategoryExecutionException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
				return modelMap;
			}
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "至少要输入一条商品分类");
		}
		return modelMap;
	}

	@RequestMapping(value = "/removeproductcategory", method = RequestMethod.POST)
	@ResponseBody
	public Map<String, Object> removeProductCategory(Long productCategoryId, HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		if (productCategoryId != null && productCategoryId > 0) {
			try {
				ProductCategoryExecution pce = productCategoryService.deleteProductCategory(productCategoryId,
						currentShop.getShopId());
				if (pce.getState() == ProductCategoryStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", pce.getStateInfo());
				}
			} catch (ProductCategoryExecutionException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
				return modelMap;
			}
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "至少要选择一条要删除的商品分类");
		}
		return modelMap;
	}
}
