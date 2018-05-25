package com.imooc.myo2o.web.shop;

import java.io.IOException;
import java.util.HashMap;
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
import com.imooc.myo2o.dto.AwardExecution;
import com.imooc.myo2o.dto.ImageHolder;
import com.imooc.myo2o.entity.Award;
import com.imooc.myo2o.entity.PersonInfo;
import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.entity.UserShopMap;
import com.imooc.myo2o.enums.AwardStateEnum;
import com.imooc.myo2o.service.AwardService;
import com.imooc.myo2o.service.UserShopMapService;
import com.imooc.myo2o.util.CodeUtil;
import com.imooc.myo2o.util.HttpServletRequestUtil;

@Controller
@RequestMapping("/shop")
public class AwardManagementController {
	@Autowired
	private AwardService awardService;

	@Autowired
	private UserShopMapService userShopMapService;

	/**
	 * 根据传入的查询条件获取奖品信息列表以及总数
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listawardsbyshop", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listAwardsByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 从url中获取分页信息
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		// 从session中获取当前店铺信息
		Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		Long shopId = currentShop.getShopId();
		if ((pageIndex > -1) && (pageSize > -1) && (currentShop != null) && (shopId != null)) {
			// 尝试从url中去获取奖品名称,如果获取到了则依据奖品名称进行模糊查询(对应的是awarddelivercheck.html中输入奖品名称进行查询的搜索栏的功能)
			String awardName = HttpServletRequestUtil.getString(request, "awardName");
			// 拼接分页查询条件
			Award awardCondition = compactAwardCondition4Search(currentShop.getShopId(), awardName);
			// 根据查询条件进行分页查询
			AwardExecution ae = awardService.getAwardList(awardCondition, pageIndex, pageSize);
			// 拼接查询结果
			modelMap.put("awardList", ae.getAwardList());
			modelMap.put("count", ae.getCount());
			modelMap.put("success", true);
			// 从session中获取用户信息,主要是为了显示用户在店铺的总积分
			PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
			if (user != null && user.getUserId() != null) {
				// 根据userId和shopId查询积分情况
				UserShopMap userShopMap = userShopMapService.getUserShopMap(user.getUserId(), shopId);
				if (userShopMap == null) {
					// 如果查询结果为空,则表示积分为零
					modelMap.put("totalPoint", 0);
				} else {
					modelMap.put("totalPoint", userShopMap.getPoint());
				}
			}
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
		}
		return modelMap;
	}

	/**
	 * 根据awardId获取award信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getawardbyid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getAwardbyId(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		long awardId = HttpServletRequestUtil.getLong(request, "awardId");
		if (awardId > -1) {
			Award award = awardService.getAwardById(awardId);
			modelMap.put("award", award);
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty awardId");
		}
		return modelMap;
	}

	/**
	 * 添加奖品(是添加一种新的奖品到奖品池,不是指兑换奖品)
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/addaward", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> addAward(HttpServletRequest request) throws IOException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 添加奖品只有一个途径,肯定要进行验证码校验
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		ObjectMapper mapper = new ObjectMapper();
		Award award = null;
		String awardStr = HttpServletRequestUtil.getString(request, "awardStr");
		MultipartHttpServletRequest multipartRequest = null;
		CommonsMultipartFile thumbnail = null;
		ImageHolder imageHolder = null;
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if (multipartResolver.isMultipart(request)) {
			multipartRequest = (MultipartHttpServletRequest) request;
			thumbnail = (CommonsMultipartFile) multipartRequest.getFile("thumbnail");
			imageHolder = new ImageHolder(thumbnail.getInputStream(), thumbnail.getOriginalFilename());
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "上传图片不能为空");
			return modelMap;
		}
		try {
			award = mapper.readValue(awardStr, Award.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		if (award != null && thumbnail != null) {
			try {
				Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
				award.setShopId(currentShop.getShopId());
				AwardExecution ae = awardService.addAward(award, imageHolder);
				if (ae.getState() == AwardStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", ae.getStateInfo());
				}
			} catch (RuntimeException e) {
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

	/**
	 * 修改奖品
	 * 
	 * @param request
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/modifyaward", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> modifyAward(HttpServletRequest request) throws IOException {
		// 这个和商品的管理是类似,在商品管理页面也有一个上下架的功能,直接在商品管理中上下架商品可以不用输入验证码
		// 所以先要去获取是否需要验证码的状态值
		boolean statusChange = HttpServletRequestUtil.getBoolean(request, "statusChange");
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 根据传入的状态值决定要不要输入验证码(只有当statusChange为false的时候才需要去检查验证码)
		if (!statusChange && !CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		// 创建一个ObjectMapper,用于将json字符串转换为实体类
		ObjectMapper mapper = new ObjectMapper();
		Award award = null;
		// 从请求头中获取awardStr信息,这个"awardStr"JSON字符串中包含了很多信息;
		String awardStr = HttpServletRequestUtil.getString(request, "awardStr");
		MultipartHttpServletRequest multipartRequest = null;
		CommonsMultipartFile thumbnail = null;
		ImageHolder imageHolder = null;
		// 创建一个图片流解析器
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// 如果含有文件流
		if (multipartResolver.isMultipart(request)) {
			multipartRequest = (MultipartHttpServletRequest) request;
			thumbnail = (CommonsMultipartFile) multipartRequest.getFile("thumbnail");
			imageHolder = new ImageHolder(thumbnail.getInputStream(), thumbnail.getOriginalFilename());
		}
		try {
			// 将awardStr的JSON字符串转化为实体类
			award = mapper.readValue(awardStr, Award.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		if (award != null) {
			try {
				// 获取当前店铺信息
				Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
				// 将当前店铺id设置到要被更新的award实体类中
				award.setShopId(currentShop.getShopId());
				AwardExecution pe = awardService.modifyAward(award, imageHolder);
				if (pe.getState() == AwardStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", pe.getStateInfo());
				}
			} catch (RuntimeException e) {
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

	/**
	 * 拼接查询条件的方法
	 * 
	 * @param shopId
	 * @param awardName
	 * @return
	 */
	private Award compactAwardCondition4Search(long shopId, String awardName) {
		Award awardCondition = new Award();
		awardCondition.setShopId(shopId);
		// 尝试去获取奖品名称,如果获取到了则依据奖品名称进行模糊查询(对应的是awarddelivercheck.html中输入奖品名称进行查询的搜索栏的功能)
		if (awardName != null) {
			awardCondition.setAwardName(awardName);
		}
		return awardCondition;
	}

}
