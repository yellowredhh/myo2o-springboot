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

	@RequestMapping(value = "/ownerlogincheck", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> ownerLoginCheck(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		boolean needVerify = HttpServletRequestUtil.getBoolean(request, "needVerify");
		if (needVerify && !CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		String userName = HttpServletRequestUtil.getString(request, "userName");
		String password = HttpServletRequestUtil.getString(request, "password");
		if (userName != null && password != null) {
			password = MD5.getMd5(password);
			LocalAuth localAuth = localAuthService.getLocalAuthByUserNameAndPwd(userName, password);
			if (localAuth != null) {
				if (localAuth.getPersonInfo().getShopOwnerFlag() == 1) {
					modelMap.put("success", true);
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

	@RequestMapping(value = "/ownerregister", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> ownerRegister(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		ObjectMapper mapper = new ObjectMapper();
		LocalAuth localAuth = null;
		String localAuthStr = HttpServletRequestUtil.getString(request, "localAuthStr");
		MultipartHttpServletRequest multipartRequest = null;
		CommonsMultipartFile profileImg = null;
		ImageHolder imageHolder = null;
		CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver(
				request.getSession().getServletContext());
		if (multipartResolver.isMultipart(request)) {
			multipartRequest = (MultipartHttpServletRequest) request;
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
			localAuth = mapper.readValue(localAuthStr, LocalAuth.class);
		} catch (Exception e) {
			modelMap.put("success", false);
			modelMap.put("errMsg", e.toString());
			return modelMap;
		}
		if (localAuth != null && localAuth.getPassword() != null && localAuth.getUserName() != null) {
			try {
				PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
				if (user != null && localAuth.getPersonInfo() != null) {
					localAuth.getPersonInfo().setUserId(user.getUserId());
				}
				localAuth.getPersonInfo().setShopOwnerFlag(1);
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
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/bindlocalauth", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> bindLocalAuth(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//验证码校验
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		//获取用户账号名字和密码
		String userName = HttpServletRequestUtil.getString(request, "userName");
		String password = HttpServletRequestUtil.getString(request, "password");
		//从session中获取用户信息(用户一旦通过微信登录之后,便能获取到用户的信息)
		PersonInfo user = (PersonInfo) request.getSession().getAttribute("user");
		//非空判定,要求账号密码和当前的用户session非空
		if (userName != null && password != null && user != null && user.getUserId() != null) {
			password = MD5.getMd5(password);
			//创建localAuth对象并且赋值
			LocalAuth localAuth = new LocalAuth();
			localAuth.setUserName(userName);
			localAuth.setPassword(password);
			localAuth.setUserId(user.getUserId());
			//绑定账号
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
	 * @param request
	 * @return
	 */
	@RequestMapping(value = "/changelocalpwd", method = RequestMethod.POST)
	@ResponseBody
	private Map<String, Object> changeLocalPwd(HttpServletRequest request) {
		Map<String, Object> modelMap = new HashMap<String, Object>();
		//验证码校验
		if (!CodeUtil.checkVerifyCode(request)) {
			modelMap.put("success", false);
			modelMap.put("errMsg", "输入了错误的验证码");
			return modelMap;
		}
		//获取用户账号名称,现用密码,要更改的新密码
		String userName = HttpServletRequestUtil.getString(request, "userName");
		String password = HttpServletRequestUtil.getString(request, "password");
		String newPassword = HttpServletRequestUtil.getString(request, "newPassword");
		//从session中获取user信息
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
		request.getSession().setAttribute("user", null);
		request.getSession().setAttribute("shopList", null);
		request.getSession().setAttribute("currentShop", null);
		modelMap.put("success", true);
		return modelMap;
	}
}
