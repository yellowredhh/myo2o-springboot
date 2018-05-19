package com.imooc.myo2o.interceptor.shop;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.imooc.myo2o.entity.PersonInfo;

/**
 * 店家管理系统登录验证拦截器
 * @author hh
 *
 */
public class ShopLoginInterceptor extends HandlerInterceptorAdapter {
	/**
	 * 主要做事前拦截,即用户操作发生前,改写preHandle里的逻辑,进行拦截.
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {
		//从session中取出用户信息来
		Object userObj = request.getSession().getAttribute("user");
		if (userObj != null) {
			//若session中的用户信息不为空,则将其转换为personInfo实体类对象.
			PersonInfo user = (PersonInfo) userObj;
			//做空值判定,确保user,userId不为空并且账号可用状态为1,确保是店家登录而不是顾客登录
			if (user != null && user.getUserId() != null && user.getUserId() > 0 && user.getEnableStatus() == 1
					&& user.getShopOwnerFlag() == 1)
				//若通过验证则返回true,拦截器返回true之后,用户接下来的操作得以继续执行.
				return true;
		}
		//如果不满足登录验证,则直接返回到登录界面,并且返回false禁止继续操作
		PrintWriter out = response.getWriter();
		out.println("<html>");
		out.println("<script>");
		out.println("window.open ('" + request.getContextPath() + "/shop/ownerlogin','_self')");
		out.println("</script>");
		out.println("</html>");
		return false;
	}
}
