package com.imooc.myo2o.util;

import javax.servlet.http.HttpServletRequest;

/*
 * 判断从前端有用户输入的验证码和后台生成的验证码是否一致.
 */
public class CodeUtil {
	public static boolean checkVerifyCode(HttpServletRequest request) {
		//这个是后台生成的验证码:称为预期验证码
		String verifyCodeExpected = (String) request.getSession()
				.getAttribute(com.google.code.kaptcha.Constants.KAPTCHA_SESSION_KEY);
		//这个是用户输入的验证码,即实际的验证码
		String verifyCodeActual = HttpServletRequestUtil.getString(request, "verifyCodeActual");
		if (verifyCodeActual == null || !verifyCodeActual.equalsIgnoreCase(verifyCodeExpected)) {
			return false;
		}
		return true;
	}
}
