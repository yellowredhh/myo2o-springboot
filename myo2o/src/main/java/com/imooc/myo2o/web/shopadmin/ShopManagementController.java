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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.myo2o.dto.ImageHolder;
import com.imooc.myo2o.dto.ShopExecution;
import com.imooc.myo2o.entity.Area;
import com.imooc.myo2o.entity.PersonInfo;
import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.entity.ShopCategory;
import com.imooc.myo2o.enums.ShopStateEnums;
import com.imooc.myo2o.service.AreaService;
import com.imooc.myo2o.service.ShopCategoryService;
import com.imooc.myo2o.service.ShopService;
import com.imooc.myo2o.util.CodeUtil;
import com.imooc.myo2o.util.HttpServletRequestUtil;

@Controller
@RequestMapping("/shopadmin")
public class ShopManagementController {

	@Autowired
	private ShopService shopService;

	@Autowired
	private AreaService areaService;

	@Autowired
	private ShopCategoryService shopCategoryService;

	@RequestMapping(value = "/getshopbyid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopById(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Long shopId = HttpServletRequestUtil.getLong(request, "shopId");
		if (shopId > -1) {
			try {
				Shop shop = shopService.queryShopByShopId(shopId);
				List<Area> areaList = areaService.getAreaList();
				modelMap.put("shop", shop);
				modelMap.put("areaList", areaList);
				modelMap.put("success", true);
			} catch (Exception e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			}
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty shopId");
		}
		return modelMap;
	}

	@RequestMapping(value = "/getshopinitinfo", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopInitInfo() {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		try {
			List<ShopCategory> shopCategoryList = shopCategoryService.getShopCategoryList(null);
			List<Area> areaList = areaService.getAreaList();
			modelMap.put("shopCategoryList", shopCategoryList);
			modelMap.put("areaList", areaList);
			modelMap.put("success", true);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
		}
		return modelMap;
	}

	@RequestMapping(value = "/registershop", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> registerShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//判断验证码输入是否正确
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "验证码输入错误");
			return modelMap;
		}
		Shop shop = null;
		//1.接受并转化相应的参数,包括店铺信息,图片信息等
		String shopStr = HttpServletRequestUtil.getString(request, "shopStr");
		ObjectMapper mapper = new ObjectMapper();
		try {
			//将从前端传过来的JSON字符串转换为Shop对象.
			shop = mapper.readValue(shopStr, Shop.class);
		} catch (IOException e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			e.printStackTrace();
			return modelMap;
		}
		//解析传入的图片
		CommonsMultipartFile shopImg = null;
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if (commonsMultipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest MultiparthttpServletRequest = (MultipartHttpServletRequest) request;
			shopImg = (CommonsMultipartFile) MultiparthttpServletRequest.getFile("shopImg");
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "上传图片不能为空");
			return modelMap;
		}
		//2.注册店铺
		if (shop != null & shopImg != null) {
			//这个店主的信息以后通过session可以获取到,现在先硬编码一下,
			//注意硬编码也不要乱写,这个OwnerId是一个外键,和person_info表关联的.
			request.getSession().setAttribute("ownerId", 9L);
			Long ownerId = (Long) request.getSession().getAttribute("ownerId");
			shop.setOwnerId(ownerId);
			//			//这里需要一个CommonsMultipartFile转换为File的方法.
			//			File shopImgFile = new File(PathUtil.getImgBasePath() + ImageUtils.getRandomFileName());
			//			try {
			//				//这里创建出了shopImgFile文件作为一个用于转换的临时文件.
			//				shopImgFile.createNewFile();
			//			} catch (IOException e) {
			//				modelMap.put("success", false);
			//				modelMap.put("errMsg", e.getMessage());
			//				return modelMap;
			//			}
			//			try {
			//				inputStreamToFile(shopImg.getInputStream(), shopImgFile);
			//			} catch (IOException e) {
			//				modelMap.put("success", false);
			//				modelMap.put("errMsg", e.getMessage());
			//				return modelMap;
			//			}
			ShopExecution se;
			try {
				//这个addShop()方法传入的后两个参数之所以不合并成传入一个CommonsMultipartFile类型,是因为在做测试的时候CommonsMultpartFile类型很难弄出来.权衡利弊还是多传入一个参数比较好.

				ImageHolder imageHolder = new ImageHolder(shopImg.getInputStream(), shopImg.getOriginalFilename());
				se = shopService.addShop(shop, imageHolder);
				if (se.getState() == ShopStateEnums.CHECK.getState()) {
					modelMap.put("success", true);
					//一个用户可以拥有多个商铺,可以从session中拿到shopList(这个shopList是在shopManagementController的getShopList方法设置到session中去得).
					List<Shop> shopList = (List<Shop>) request.getSession().getAttribute("shopList");
					if (shopList == null || shopList.size() == 0) {
						shopList = new ArrayList<Shop>();
					}
					shopList.add(se.getShop());
					request.getSession().setAttribute("shopList", shopList);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());
				}
			} catch (IOException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
				return modelMap;
			}
			/*
			 * 网络上流传的CommonsMultipartFile转File的方法行不通,坑啊
			DiskFileItem fi = (DiskFileItem)(shopImg.getFileItem()); 
			File f = fi.getStoreLocation();
			*/
			return modelMap;
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入店铺信息");
			return modelMap;
		}
	}

	/*
	 * 修改店铺信息
	 */
	@RequestMapping(value = "/modifyshop", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> modifyShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//判断验证码输入是否正确
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "验证码输入错误");
			return modelMap;
		}

		Shop shop = null;
		//1.接受并转化相应的参数,包括店铺信息,图片信息等
		String shopStr = HttpServletRequestUtil.getString(request, "shopStr");
		ObjectMapper mapper = new ObjectMapper();
		try {
			//将从前端传过来的JSON字符串转换为Shop对象.
			shop = mapper.readValue(shopStr, Shop.class);
		} catch (IOException e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
			e.printStackTrace();
			return modelMap;
		}
		//解析传入的图片
		CommonsMultipartFile shopImg = null;
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if (commonsMultipartResolver.isMultipart(request)) {
			MultipartHttpServletRequest MultiparthttpServletRequest = (MultipartHttpServletRequest) request;
			shopImg = (CommonsMultipartFile) MultiparthttpServletRequest.getFile("shopImg");
		}
		//2.修改店铺信息
		if (shop != null & shop.getShopId() != null) {
			ShopExecution se;
			try {
				if (shop.getShopImg() == null) {
					se = shopService.modifyShop(shop, null);
				} else {
					ImageHolder imageHolder = new ImageHolder(shopImg.getInputStream(), shopImg.getOriginalFilename());
					se = shopService.modifyShop(shop, imageHolder);
				}
				if (se.getState() == ShopStateEnums.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", se.getStateInfo());
				}
			} catch (IOException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
				return modelMap;
			}

			return modelMap;
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入店铺ID");
			return modelMap;
		}
	}

	@RequestMapping(value = "/getshopmanagementinfo", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopManagementInfo(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Long shopId = HttpServletRequestUtil.getLong(request, "shopId");
		if (shopId <= 0) {
			Object currentShopObj = request.getSession().getAttribute("currentShop");
			if (currentShopObj == null) {
				modelMap.put("redirect", true);
				//本来url的value应该是'myo2o/shopadmin/shoplist'才对,但是不知道为什么重定向的url都会自动携带原url的父目录,导致最后地址变为'myo2o/shopadmin/myo2o/shopadmin/shoplist'
				modelMap.put("url", "shoplist");
			} else {
				Shop currentShop = (Shop) currentShopObj;
				modelMap.put("redirect", false);
				modelMap.put("shopId", currentShop.getShopId());
			}
		} else {
			Shop currentShop = new Shop();
			currentShop.setShopId(shopId);
			request.getSession().setAttribute("currentShop", currentShop);
			modelMap.put("redirect", false);
		}
		return modelMap;
	}

	@RequestMapping(value = "/getshoplist", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getShopList(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//先硬编码一个数据到session中去.
		request.getSession().setAttribute("areaId", 4L);
		//硬编码微信相关的数据到session中去,避免每一次调试都要去访问微信url
		PersonInfo user = new PersonInfo();
		user.setUserId(9L);
		user.setName("from_shopmanagementcontroller");
		request.getSession().setAttribute("user", user);
		user = (PersonInfo) request.getSession().getAttribute("user");
		Long areaId = (Long) request.getSession().getAttribute("areaId");
		try {
			Shop shopCondition = new Shop();
			Area area = new Area();
			area.setAreaId(areaId);
			shopCondition.setArea(area);
			ShopExecution se = shopService.getShopList(shopCondition, 0, 100);
			//列出店铺列表成功之后,就将当前用户可操作的店铺列表放到session中去,作为权限验证依据,即该账号只能操作自己拥有的店铺
			request.getSession().setAttribute("shopList", se.getShopList());
			modelMap.put("shopList", se.getShopList());
			modelMap.put("success", true);
			modelMap.put("areaId", areaId);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.getMessage());
		}
		return modelMap;
	}

	//后来发现thumbnails的of方法可以接受一个InputStream参数,所以对代码做了比较大的改动,不需要这个转换方法了
	//
	//	/*
	//	 * CommonsMultipartFile有一个getInputStream方法,所以可以创建一个InputStream转File的方法.
	//	 */
	//	private static void inputStreamToFile(InputStream ins, File file) {
	//		FileOutputStream fos = null;
	//		try {
	//			fos = new FileOutputStream(file);
	//			int count = 0;
	//			byte[] byteBuffer = new byte[1024];
	//			if (ins.read(byteBuffer) != -1) {
	//				fos.write(byteBuffer, 0, count);
	//			}
	//		} catch (IOException e) {
	//			throw new RuntimeException("创建io流失败");
	//		} finally {
	//			try {
	//				if (ins != null) {
	//					ins.close();
	//				}
	//				if (fos != null) {
	//					fos.close();
	//				}
	//			} catch (Exception e) {
	//				throw new RuntimeException("关闭io流失败");
	//			}
	//		}
	//
	//	}
	//
}
