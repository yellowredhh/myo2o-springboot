package com.imooc.myo2o.web.shop;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import com.imooc.myo2o.dto.LocalAuthExecution;
import com.imooc.myo2o.entity.LocalAuth;
import com.imooc.myo2o.entity.PersonInfo;
import com.imooc.myo2o.enums.LocalAuthStateEnum;
import com.imooc.myo2o.service.LocalAuthService;
import com.imooc.myo2o.util.CodeUtil;
import com.imooc.myo2o.util.HttpServletRequestUtil;
import com.imooc.myo2o.util.MD5;

@Controller
@RequestMapping(value = "shop", method = { RequestMethod.GET, RequestMethod.POST })
public class OwnerAuthController {
	@Autowired
	private LocalAuthService localAuthService;

	/**
	 * 店铺拥有着登录店铺管家系统的方法
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/ownerlogincheck", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> ownerLoginCheck(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 是否需要验证码,从shop/ownerlogin页面登录进来默认是不需要的
		boolean needVerify = HttpServletRequestUtil.getBoolean(request, "needVerify");
		if (needVerify && !CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		// 拿到
		String userName = HttpServletRequestUtil.getString(request, "userName");
		String password = HttpServletRequestUtil.getString(request, "password");
		// 非空判定
		if (userName != null && password != null) {
			// 对输入的密码进行加密(因为数据库中存储的密码是经过MD5加密的)
			password = MD5.getMd5(password);
			// 通过账号名字和密码获取到本地账号实体类对象
			LocalAuth localAuth = localAuthService.getLocalAuthByUserNameAndPwd(userName, password);
			if (localAuth != null) {
				// 判断这个登陆的账号密码是否是店主(如果这个实体类对象包含的personInfo对象的shopOwnerFlag为1表示是店主,否则不是店主)
				if (localAuth.getPersonInfo().getShopOwnerFlag() == 1) {
					modelMap.put("success", true);
					// 登录成功,把店主信息放到session中
					request.getSession().setAttribute("user", localAuth.getPersonInfo());
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", "非管理员没有权限访问");
				}
			} else {
				modelMap.put("success", false);
				modelMap.put("errMsg", "用户名或密码错误");
			}
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "用户名和密码均不能为空");
		}
		return modelMap;
	}

	/**
	 * 店主注册的方法
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/ownerregister", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> ownerRegister(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 验证码校验
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		ObjectMapper mapper = new ObjectMapper();
		LocalAuth localAuth = null;
		// 获取post请求体中的要注册的本地账号数据
		String localAuthStr = HttpServletRequestUtil.getString(request, "localAuthStr");
		MultipartHttpServletRequest multipartRequest = null;
		CommonsMultipartFile profileImg = null;
		ImageHolder imageHolder = null;
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		// 如果请求中含有文件流(也就是头像图片),则获取头像
		if (multipartResolver.isMultipart(request)) {
			multipartRequest = (MultipartHttpServletRequest) request;
			// 获取头像
			profileImg = (CommonsMultipartFile) multipartRequest.getFile("thumbnail");
			try {
				imageHolder = new ImageHolder(profileImg.getInputStream(), profileImg.getOriginalFilename());
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "上传图片不能为空");
			return modelMap;
		}
		try {
			// 将json字符串解析成实体类对象
			localAuth = mapper.readValue(localAuthStr, LocalAuth.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		// 非空判定
		if (localAuth != null && localAuth.getPassword() != null && localAuth.getUserName() != null) {
			try {
				//
				PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
				if (user != null && localAuth.getPersonInfo() != null) {
					localAuth.getPersonInfo().setUserId(user.getUserId());
				}
				// 设置顾客flag为0表示不是顾客(顾客和管理员flag必须设置为0,否则会报sql错误)
				localAuth.getPersonInfo().setCustomerFlag(0);
				// 设置店主flag为1表示是店主
				localAuth.getPersonInfo().setShopOwnerFlag(1);
				// 设置管理员flag为0表示不是管理员
				localAuth.getPersonInfo().setAdminFlag(0);
				LocalAuthExecution le = localAuthService.register(localAuth, imageHolder);
				if (le.getState() == LocalAuthStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", le.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}

		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入注册信息");
		}
		return modelMap;
	}

	/**
	 * 绑定微信账号的方法
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/bindlocalauth", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> bindLocalAuth(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 验证码校验
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		// 获取用户账号名字和密码
		String userName = HttpServletRequestUtil.getString(request, "userName");
		String password = HttpServletRequestUtil.getString(request, "password");
		// 从session中获取用户信息(用户一旦通过微信登录之后,便能获取到用户的信息)
		PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
		// 非空判定,要求账号密码和当前的用户session非空
		if (userName != null && password != null && user != null && user.getUserId() != null) {
			password = MD5.getMd5(password);
			// 创建localAuth对象并且赋值
			LocalAuth localAuth = new LocalAuth();
			localAuth.setUserName(userName);
			localAuth.setPassword(password);
			localAuth.setUserId(user.getUserId());
			// 绑定账号
			LocalAuthExecution le = localAuthService.bindLocalAuth(localAuth);
			if (le.getState() == LocalAuthStateEnum.SUCCESS.getState()) {
				modelMap.put("success", true);
			} else {
				modelMap.put("success", false);
				modelMap.put("errMsg", le.getStateInfo());
			}
		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "用户名和密码均不能为空");
		}
		return modelMap;
	}

	/**
	 * 更改用户密码的方法
	 * 
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/changelocalpwd", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> changeLocalPwd(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// 验证码校验
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		// 获取用户账号名称,现用密码,要更改的新密码
		String userName = HttpServletRequestUtil.getString(request, "userName");
		String password = HttpServletRequestUtil.getString(request, "password");
		String newPassword = HttpServletRequestUtil.getString(request, "newPassword");
		// 从session中获取user信息
		PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
		long employeeId = 0;
		if (user != null && user.getUserId() != null) {
			employeeId = user.getUserId();
		} else {
			employeeId = 1;
		}
		if (userName != null && password != null && newPassword != null && employeeId > 0
				&& !password.equals(newPassword)) {
			try {
				LocalAuthExecution le = localAuthService.modifyLocalAuth(employeeId, userName, password, newPassword);
				if (le.getState() == LocalAuthStateEnum.SUCCESS.getState()) {
					modelMap.put("success", true);
				} else {
					modelMap.put("success", false);
					modelMap.put("errMsg", le.getStateInfo());
				}
			} catch (RuntimeException e) {
				modelMap.put("success", false);
				modelMap.put("errMsg", e.toString());
				return modelMap;
			}

		} else {
			modelMap.put("success", false);
			modelMap.put("errMsg", "请输入密码");
		}
		return modelMap;
	}

	/**
	 * 注销(退出登录),清空session
	 * 
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> ownerLogoutCheck(HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		// session中放了三个信息:
		// 一个是user也就是当前登录用户的信息;
		request.getSession().setAttribute("user", null);
		// 一个是shopList也就是当前登录用户所拥有的所有的店铺的列表
		request.getSession().setAttribute("shopList", null);
		// 一个是currentShop也就是当前店铺的信息
		request.getSession().setAttribute("currentShop", null);
		modelMap.put("success", true);
		return modelMap;
	}
}
