package com.imooc.myo2o.web.shopadmin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.myo2o.Exceptions.ProductExecutionException;
import com.imooc.myo2o.dto.ImageHolder;
import com.imooc.myo2o.dto.ProductExecution;
import com.imooc.myo2o.entity.Product;
import com.imooc.myo2o.entity.ProductCategory;
import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.enums.ProductStateEnum;
import com.imooc.myo2o.service.ProductCategoryService;
import com.imooc.myo2o.service.ProductService;
import com.imooc.myo2o.util.CodeUtil;
import com.imooc.myo2o.util.HttpServletRequestUtil;

@Controller
@RequestMapping("/shopadmin")
public class ProductManagementController {

	@Autowired
	private ProductService productService;
	@Autowired
	private ProductCategoryService productCategoryService;

	//支持上传的商品详情图片的最大数量
	private static final int IMAGEMAXCOUNT = 6;

	@RequestMapping(value = "/addproduct", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> addProduct(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//验证码校验
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		//接收前端参数的变量的初始化,包括商品,缩略图,商品详情图片列表实体类
		ObjectMapper mapper = new ObjectMapper();
		Product product = null;
		MultipartHttpServletRequest multipartRequest = null;
		ImageHolder thumbnail = null;
		List<ImageHolder> productImgs = new ArrayList<ImageHolder>();
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		try {
			//若请求中存在文件流,则取出文件(包括缩略图和详情图)
			if (multipartResolver.isMultipart(request)) {
				thumbnail = handleImage(request, thumbnail, productImgs);
			} else {
				modelMap.put("success", false);
				modelMap.put("errMsg", "上传图片不能为空");
				return modelMap;
			}
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}

		//获取前端传入的productStr的JSON字符串并且将其转化为实体类
		String productStr = HttpServletRequestUtil.getString(request, "productStr");
		try {
			product = mapper.readValue(productStr, Product.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		//若商品信息,商品缩略图,商品详情图列表都不为空,则开始进行商品的添加.
		if (product != null && thumbnail != null && productImgs.size() > 0) {
			try {
				//从session中获取shopId并将其赋值给product,减少对前端由用户手动传入数据的依赖
				Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
				product.setShop(currentShop);
				ProductExecution pe = productService.addProduct(product, thumbnail, productImgs);
				if (pe.getState() == ProductStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", pe.getStateInfo());
				}
			} catch (ProductExecutionException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}

		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入商品信息");
		}
		return modelMap;
	}

	@RequestMapping(value = "/getproductbyproductid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getProductByProductId(@RequestParam Long productId) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//非空判断
		if (productId > -1) {
			//获取商品信息
			Product product = productService.getProductByProductId(productId);
			//获取该店铺下的商品分类列表
			List<ProductCategory> productCategoryList = productCategoryService
					.getProductCategoryList(product.getShop().getShopId());
			modelMap.put("product", product);
			modelMap.put("productCategoryList", productCategoryList);
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty productId");
		}
		return modelMap;
	}

	@RequestMapping(value = "/modifyproduct", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> modifyProduct(HttpServletRequest request) {
		//这里开了一个口子,用于判断是在商品编辑的时候调用还是在上下架操作的时候调用
		//如果是商品编辑,则需要判断验证码,如果是上下架,则不需要判断验证码
		boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//statusChange为false表示商品编辑界面,需要验证码
		//statusChange为true表示直接进行上下架操作,不需要验证码
		if (!statusChange && !CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		//接收前端参数的变量的初始化,包括商品,缩略图,商品详情图片列表实体类
		ObjectMapper mapper = new ObjectMapper();
		Product product = null;
		MultipartHttpServletRequest multipartRequest = null;
		ImageHolder thumbnail = null;
		List<ImageHolder> productImgs = new ArrayList<ImageHolder>();
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		try {
			//若请求中存在文件流,则取出文件(包括缩略图和详情图),如果不包含文件流,则表示不需要对图片进行更新
			if (multipartResolver.isMultipart(request)) {
				thumbnail = handleImage(request, thumbnail, productImgs);
			}
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}

		//获取前端传入的productStr的JSON字符串
		String productStr = HttpServletRequestUtil.getString(request, "productStr");
		try {
			//尝试将获取到的前段传过来的表单String流转化为实体类
			product = mapper.readValue(productStr, Product.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		//若商品信息不为空,则更新商品
		if (product != null) {
			try {
				//从session中获取shopId并将其赋值给product,减少对前端由用户手动传入数据的依赖
				Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
				product.setShop(currentShop);
				//商品更新操作
				ProductExecution pe = productService.modifyProduct(product, thumbnail, productImgs);
				if (pe.getState() == ProductStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", pe.getStateInfo());
				}
			} catch (ProductExecutionException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}

		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入商品信息");
		}
		return modelMap;
	}

	/*
	 * 查询某一个店铺下的所有的商品(分页查询)
	 */
	@RequestMapping(value = "/getproductlistbyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getProductListByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//获取前台传过来的页码
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		//获取前台传过来的每页显示数量上限
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		//获取当前店铺信息,主要是shopId
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		//分页查询空值判断
		if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null) && (currentShop.getShopId() != null)) {
			//获取传入的进行分页查询的条件,包括商品的模糊名,商品类别.这些条件可以进行组合使用
			long productCategoryId = HttpServletRequestUtil.getLong(request, "productCategoryId");
			String productName = HttpServletRequestUtil.getString(request, "productName");
			//整合获取到的查询条件
			Product productCondition = compactProductCondition4Search(currentShop.getShopId(), productCategoryId,
					productName);
			//分页查询
			ProductExecution pe = productService.getProductList(productCondition, pageIndex, pageSize);
			modelMap.put("productList", pe.getProductList());
			modelMap.put("count", pe.getCount());
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
		}
		return modelMap;
	}

	//对零散的查询条件进行包装整合.
	private Product compactProductCondition4Search(long shopId, long productCategoryId, String productName) {
		Product productCondition = new Product();
		Shop shop = new Shop();
		shop.setShopId(shopId);
		productCondition.setShop(shop);
		if (productCategoryId != -1L) {
			ProductCategory productCategory = new ProductCategory();
			productCategory.setProductCategoryId(productCategoryId);
			productCondition.setProductCategory(productCategory);
		}
		if (productName != null) {
			productCondition.setProductName(productName);
		}
		return productCondition;
	}

	//重构出了处理商品缩略图和商品详情图的方法
	private ImageHolder handleImage(HttpServletRequest request, ImageHolder thumbnail, List<ImageHolder> productImgs)
			throws IOException {
		MultipartHttpServletRequest multipartRequest;
		multipartRequest = (MultipartHttpServletRequest) request;
		//取出缩略图并且构建ImageHolder对象(缩略图名字和前端进行约定)
		CommonsMultipartFile thumbnailFile = (CommonsMultipartFile) multipartRequest.getFile("thumbnail");
		if (thumbnailFile != null) {
			thumbnail = new ImageHolder(thumbnailFile.getInputStream(), thumbnailFile.getOriginalFilename());
		} //取出详情图列表并且构建List<ImageHolder>列表对象.(详情图名字和前端进行约定)
		for (int i = 0; i < IMAGEMAXCOUNT; i++) {
			//这个序列是和前端传入的文件名做好约定的.最多允许i从0到5一共6个文件.
			CommonsMultipartFile productImg = (CommonsMultipartFile) multipartRequest.getFile("productImg" + i);
			if (productImg != null) {
				//若取出的商品详情图片不为空,则添加到详情图片列表中.
				ImageHolder productImageHolder = new ImageHolder(productImg.getInputStream(),
						productImg.getOriginalFilename());
				productImgs.add(productImageHolder);
			} else {
				break;
			}
		}
		return thumbnail;
	}
}
