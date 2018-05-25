package com.imooc.myo2o.web.shop;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.myo2o.dto.ShopAuthMapExecution;
import com.imooc.myo2o.dto.UserAwardMapExecution;
import com.imooc.myo2o.entity.Award;
import com.imooc.myo2o.entity.PersonInfo;
import com.imooc.myo2o.entity.ShopAuthMap;
import com.imooc.myo2o.entity.UserAwardMap;
import com.imooc.myo2o.enums.UserAwardMapStateEnum;
import com.imooc.myo2o.service.AwardService;
import com.imooc.myo2o.service.PersonInfoService;
import com.imooc.myo2o.service.ShopAuthMapService;
import com.imooc.myo2o.service.UserAwardMapService;
import com.imooc.myo2o.util.HttpServletRequestUtil;
import com.imooc.myo2o.util.weixin.message.req.WechatInfo;

@Controller
@RequestMapping("/shop")
public class UserAwardManagementController {
	@Autowired
	private UserAwardMapService userAwardMapService;
	@Autowired
	private AwardService awardService;
	@Autowired
	private PersonInfoService personInfoService;
	@Autowired
	private ShopAuthMapService shopAuthMapService;

	/**
	 * 根据查询条件对用户奖品映射进行查询(就是查询奖品兑换记录)
	 * 之前叫:listuserawardmapsbyshop,名字还是有点不好,奖品记录应该是根据用户来,因为我要实现的是侧边栏中的"兑换记录"的功能,所以不能根据shop来
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/listuserawardmapsbycustomer", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> listUserAwardMapsByShop(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 从session中获取店铺信息
		// Shop currentShop = (Shop) request.getSession().getAttribute("currentShop");
		// 从session中获取用户信息
		PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
		Long userId = user.getUserId();
		// 分页信息
		int pageIndex = HttpServletRequestUtil.getInt(request, "pageIndex");
		int pageSize = HttpServletRequestUtil.getInt(request, "pageSize");
		//
		if ((pageIndex > -1) && (pageSize > -1) && (user != null) && (userId != null)) {
			UserAwardMap userAwardMapCondition = new UserAwardMap();
			userAwardMapCondition.setUserId(userId);
			// 尝试获取shopId
			long shopId = HttpServletRequestUtil.getLong(request, "shopId");
			// 如果存在shopId,则要查询的是用户在该shopId的店铺下面的兑换记录
			if (shopId > -1) {
				userAwardMapCondition.setShopId(shopId);
			}
			UserAwardMapExecution ue = userAwardMapService.listUserAwardMap(userAwardMapCondition, pageIndex, pageSize);
			modelMap.put("userAwardMapList", ue.getUserAwardMapList());
			modelMap.put("count", ue.getCount());
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty pageSize or pageIndex or shopId");
		}
		return modelMap;
	}

	/**
	 * 根据顾客奖品映射id获取单条顾客奖品信息
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/getawardbyuserawardid", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getAwardById(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		Long userAwardMapId = HttpServletRequestUtil.getLong(request, "userAwardId");
		// 空值判定
		if (userAwardMapId > -1) {
			UserAwardMap userAwardMap = userAwardMapService.getUserAwardMapById(userAwardMapId);
			// 其实我觉得直接从UserAwardMap实体类中也可以去除award实体类的,毕竟在UserAwardMap中是关联了award的
			Award award = awardService.getAwardById(userAwardMap.getAwardId());
			modelMap.put("award", award);
			modelMap.put("usedStatus", userAwardMap.getUsedStatus());
			modelMap.put("userAwardMap", userAwardMap);
			modelMap.put("success", true);
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "empty userAwardMapId");
		}
		return modelMap;
	}

	/**
	 * 兑换奖品的方法
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/adduserawardmap", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> addUserAwardMap(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 从session中获取user信息
		PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
		// 从url中获取awardId
		Long awardId = HttpServletRequestUtil.getLong(request, "awardId");
		// 先根据awardId查询到奖品,然后将user和award都设置到userAwardMap中
		UserAwardMap userAwardMap = compactUserAwardMap4Add(user, awardId);
		if (userAwardMap != null) {
			try {
				// 兑换奖品
				UserAwardMapExecution uame = userAwardMapService.addUserAwardMap(userAwardMap);
				if (uame.getState() == UserAwardMapStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("sueccess", false);
					modelMap.put("errMsg", uame.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.getMessage());
			}
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请选择奖品");
		}
		return modelMap;
	}

	/**
	 * 更改用户奖品的可用状态的方法
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/exchangeaward", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> exchangeAward(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 获取用户信息
		PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
		// 获取微信回传的state信息
		String qrCodeinfo = HttpServletRequestUtil.getString(request, "state");
		ObjectMapper mapper = new ObjectMapper();
		WechatInfo wechatInfo = null;
		try {
			// 解析state信息
			wechatInfo = mapper.readValue(qrCodeinfo, WechatInfo.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		// 判断二维码是否有效
		if (!checkQRCodeInfo(wechatInfo)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "二维码信息非法！");
			return modelMap;
		}
		Long userAwardId = wechatInfo.getUserAwardId();
		Long customerId = wechatInfo.getCustomerId();
		UserAwardMap userAwardMap = compactUserAwardMap4Exchange(customerId, userAwardId);
		if (userAwardMap != null) {
			try {
				// 检查用户权限
				if (!checkShopAuth(user.getUserId(), userAwardMap)) {
					modelMap.put("success", false);
					modelMap.put("errMsg", "无操作权限");
					return modelMap;
				}
				// 更改奖品可用状态
				UserAwardMapExecution se = userAwardMapService.modifyUserAwardMap(userAwardMap);
				if (se.getState() == UserAwardMapStateEnum.SUCCESS.getState()) {
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
			modelMap.put("errMsg", "请输入领取信息");
		}
		return modelMap;
	}

	private boolean checkQRCodeInfo(WechatInfo wechatInfo) {
		if (wechatInfo != null && wechatInfo.getUserAwardId() != null && wechatInfo.getCustomerId() != null
				&& wechatInfo.getCreateTime() != null) {
			long nowTime = System.currentTimeMillis();
			if ((nowTime - wechatInfo.getCreateTime()) <= 5000) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	private UserAwardMap compactUserAwardMap4Exchange(Long customerId, Long userAwardId) {
		UserAwardMap userAwardMap = null;
		if (customerId != null && userAwardId != null) {
			userAwardMap = userAwardMapService.getUserAwardMapById(userAwardId);
			userAwardMap.setUsedStatus(0);
			userAwardMap.setUserId(customerId);
		}
		return userAwardMap;
	}

	private UserAwardMap compactUserAwardMap4Add(PersonInfo user, Long awardId) {
		UserAwardMap userAwardMap = new UserAwardMap();
		Award award = null;
		if (user != null && awardId != null) {
			award = awardService.getAwardById(awardId);
			// 1表示已经兑换
			userAwardMap.setUsedStatus(1);
			userAwardMap.setAward(award);
			userAwardMap.setUser(user);
		}
		return userAwardMap;
	}

	private boolean checkShopAuth(long userId, UserAwardMap userAwardMap) {
		ShopAuthMapExecution shopAuthMapExecution = shopAuthMapService.listShopAuthMapByShopId(userAwardMap.getShopId(),
				1, 1000);
		for (ShopAuthMap shopAuthMap : shopAuthMapExecution.getShopAuthMapList()) {
			if (shopAuthMap.getEmployeeId() == userId) {
				return true;
			}
		}
		return false;
	}
}
