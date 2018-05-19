package com.imooc.myo2o.web.wechat;

import java.io.IOException;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.imooc.myo2o.dto.ShopExecution;
import com.imooc.myo2o.dto.WechatAuthExecution;
import com.imooc.myo2o.entity.PersonInfo;
import com.imooc.myo2o.entity.WechatAuth;
import com.imooc.myo2o.enums.WechatAuthStateEnum;
import com.imooc.myo2o.service.PersonInfoService;
import com.imooc.myo2o.service.ShopAuthMapService;
import com.imooc.myo2o.service.ShopService;
import com.imooc.myo2o.service.WechatAuthService;
import com.imooc.myo2o.util.weixin.WeiXinUser;
import com.imooc.myo2o.util.weixin.WeiXinUserUtil;
import com.imooc.myo2o.util.weixin.message.pojo.UserAccessToken;


@Controller
@RequestMapping("/wechatlogin")
/**
 * 从微信菜单点击后调用的接口，可以在url里增加参数（role_type）来表明是从商家还是从玩家按钮进来的，依次区分登陆后跳转不同的页面
 * String codeUrl = "https://open.weixin.qq.com/connect/oauth2/authorize?appid=wxf0e81c3bee622d60&redirect_uri="
		+ URLEncoder.encode("www.cityrun.com", "utf-8")
		+ "&response_type=code&scope=snsapi_userinfo&state=STATE#wechat_redirect";
 * 玩家会跳转到index.html页面
 * 商家如果没有注册，会跳转到注册页面，否则跳转到任务管理页面
 * 如果是商家的授权用户登陆，会跳到授权店铺的任务管理页面
 * @author lixiang
 *
 */
public class WeiXinLoginController {

	private static Logger log = LoggerFactory.getLogger(WeiXinLoginController.class);

	@Resource
	private PersonInfoService personInfoService;
	@Resource
	private WechatAuthService WechatAuthService;

	@Resource
	private ShopService shopService;

	@Resource
	private ShopAuthMapService shopAuthMapService;

	private static final String FRONTEND = "1";
	private static final String SHOPEND = "2";
	
	//这个入口是微信公众平台,不是输入localhost:8080/myo2o/wechatlogin/logincheck
	@RequestMapping(value = "/logincheck", method = { RequestMethod.GET })
	public String doGet(HttpServletRequest request, HttpServletResponse response) {
		log.debug("weixin login get...");
		//获取微信公众号传过来的code值,通过code可以获取到access_token,进而获取到用户信息
		String code = request.getParameter("code");
		//state用来传输我们自定义的信息,方便程序调用.也可以不用
		String roleType = request.getParameter("state");
		log.debug("weixin login code:" + code);
		WechatAuth auth = null;
		WeiXinUser user = null;
		//openId是每一个用户针对于某一个微信公众号所独有的.
		String openId = null;
		if (null != code) {
			UserAccessToken token;
			try {
				//通过code获取access_token信息
				token = WeiXinUserUtil.getUserAccessToken(code);
				log.debug("weixin login token:" + token.toString());
				String accessToken = token.getAccessToken();
				openId = token.getOpenId();
				//通过access_token和openId可以获取到用户信息
				user = WeiXinUserUtil.getUserInfo(accessToken, openId);
				log.debug("weixin login user:" + user.toString());
				request.getSession().setAttribute("openId", openId);
				auth = WechatAuthService.getWechatAuthByOpenId(openId);
			} catch (IOException e) {
				log.error("error in getUserAccessToken or getUserInfo or findByOpenId: " + e.toString());
				e.printStackTrace();
			}
		}

		//获取到openId后,可以去数据库中查询是否有对应的用户账号,如果没有则创建账号(直接和网站无缝连接)
		log.debug("weixin login success.");
		log.debug("login role_type:" + roleType);

		if (FRONTEND.equals(roleType)) { //如果roleType为FRONTEND,则是想进前端管理系统(也就是想成为普通用户)
			//从微信信息中获取到用户信息
			PersonInfo personInfo = WeiXinUserUtil.getPersonInfoFromRequest(user);
			if (auth == null) {//如果用户信息为空,则转到注册帐号逻辑,去注册帐号
				personInfo.setCustomerFlag(1);
				auth = new WechatAuth();
				auth.setOpenId(openId);
				auth.setPersonInfo(personInfo);
				WechatAuthExecution we = WechatAuthService.register(auth, null);
				if (we.getState() != WechatAuthStateEnum.SUCCESS.getState()) {
					return null; //如果注册不成功则返回null
				}
			}
			personInfo = personInfoService.getPersonInfoById(auth.getUserId());
			request.getSession().setAttribute("user", personInfo);
			//如果注册成功或者已有账号则跳转到项目主页
			return "frontend/index";
		}

		if (SHOPEND.equals(roleType)) { //如果roleType为SHOPEND,则是想进店家管理系统
			PersonInfo personInfo = null;
			WechatAuthExecution we = null;
			if (auth == null) {
				auth = new WechatAuth();
				auth.setOpenId(openId);
				personInfo = WeiXinUserUtil.getPersonInfoFromRequest(user);
				personInfo.setShopOwnerFlag(1);
				auth.setPersonInfo(personInfo);
				we = WechatAuthService.register(auth, null);
				if (we.getState() != WechatAuthStateEnum.SUCCESS.getState()) {
					return null;
				}
			}
			personInfo = personInfoService.getPersonInfoById(auth.getUserId());
			request.getSession().setAttribute("user", personInfo);
			ShopExecution se = shopService.getByEmployeeId(personInfo.getUserId());
			request.getSession().setAttribute("user", personInfo);
			//如果进入店家管理系统的账号下没有店铺则进入店铺的注册页面,如果有了店铺,就显示所拥有的店铺
			if (se.getShopList() == null || se.getShopList().size() <= 0) {
				return "shop/registershop";
			} else {
				request.getSession().setAttribute("shopList", se.getShopList());
				return "shop/shoplist";
			}
		}
		return null;
	}
}
