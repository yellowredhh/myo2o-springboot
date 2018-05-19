package com.imooc.myo2o.config.web;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.servlet.ServletRegistrationBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.google.code.kaptcha.servlet.KaptchaServlet;
import com.imooc.myo2o.interceptor.shop.ShopLoginInterceptor;
import com.imooc.myo2o.interceptor.shop.ShopPermissionInterceptor;

//简化配置： (1)自动注册DefaultAnootationHandlerMapping,AnotationMethodHandlerAdapter (2)提供一些列：数据绑定，数字和日期的format @NumberFormat, @DateTimeFormat, xml,json默认读写支持
//@EnableWebMvc这个就相当于spring-web.xml中的<mvc:annotation-driven />
/**
 * 一个类实现了ApplicationContextAware接口,这个类就可以方便的获取spring容器中的所有的bean
 * 
 * @author hh
 *
 */
@Configuration
@EnableWebMvc
public class MvcConfiguration extends WebMvcConfigurerAdapter implements ApplicationContextAware {
	// Spring容器
	private ApplicationContext applicationContext;

	@Override
	public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
		this.applicationContext = applicationContext;
	}

	/**
	 * 静态资源配置静态资源默认servlet配置 (1)加入对静态资源的处理：js,gif,png,不要拦截resources路径下的所有的资源
	 * (其实我们就是把静态资源放在了这个目录下面) 交由default-servlet-handler来处理 (2)允许使用"/"做整体映射
	 *
	 * @param registry
	 */
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		// registry.addResourceHandler("/resources/**").addResourceLocations("classpath:/resources/");
		// 寻找图片的地址:必须加一个file前缀表示文件
		registry.addResourceHandler("/upload/**").addResourceLocations("file:F:/Projects/eclipse/Myo2o/upload/");

	}

	/**
	 * 配置交由default-servlet-handler来处理
	 *
	 * @param configurer
	 */
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
		configurer.enable();
	}

	/**
	 * 配置视图解析器
	 *
	 * @return
	 */
	@Bean(name = "viewResolver")
	public ViewResolver createViewResolver() {
		InternalResourceViewResolver internalResourceViewResolver = new InternalResourceViewResolver();
		// 设置Spring容器
		internalResourceViewResolver.setApplicationContext(this.applicationContext);
		// 取消缓存
		internalResourceViewResolver.setCache(false);
		// 设置解析的前缀
		internalResourceViewResolver.setPrefix("/WEB-INF/html/");
		// 设置解析的后缀
		internalResourceViewResolver.setSuffix(".html");
		return internalResourceViewResolver;
	}

	/**
	 * 文件上传解析器
	 * 
	 * @return
	 */
	@Bean(name = "multipartResolver")
	public CommonsMultipartResolver createCommonsMultipartResolver() {
		CommonsMultipartResolver commonsMultipartResolver = new CommonsMultipartResolver();
		commonsMultipartResolver.setDefaultEncoding("utf-8");
		// 上传的最大文件限制为1024*1024*20=20M
		commonsMultipartResolver.setMaxUploadSize(20971520);
		commonsMultipartResolver.setMaxInMemorySize(20971520);
		return commonsMultipartResolver;
	}

	@Value("${kaptcha.border}")
	private String border;
	@Value("${kaptcha.textproducer.font.color}")
	private String fcolor;
	@Value("${kaptcha.image.width}")
	private String width;
	@Value("${kaptcha.textproducer.char.string}")
	private String cString;
	@Value("${kaptcha.image.height}")
	private String height;
	@Value("${kaptcha.textproducer.font.size}")
	private String fsize;
	@Value("${kaptcha.noise.color}")
	private String nColor;
	@Value("${kaptcha.textproducer.char.length}")
	private String clength;
	@Value("${kaptcha.textproducer.font.names}")
	private String fnames;

	/**
	 * 配置验证码,由于没有了web.xml文件,所以在这里配置一个bean充当验证码
	 * 
	 * @return
	 */
	@Bean
	public ServletRegistrationBean servletRegistrationBean() {
		ServletRegistrationBean servletRegistrationBean = new ServletRegistrationBean(new KaptchaServlet(), "/Kaptcha");
		servletRegistrationBean.addInitParameter("kaptcha.border", border);// 无边框
		servletRegistrationBean.addInitParameter("kaptcha.textproducer.font.color", fcolor);// 字体颜色
		servletRegistrationBean.addInitParameter("kaptcha.image.width", width);// 图片宽度
		servletRegistrationBean.addInitParameter("kaptcha.textproducer.char.string", cString);// 使用哪些字符
		servletRegistrationBean.addInitParameter("kaptcha.image.height", height);// 图片高度
		servletRegistrationBean.addInitParameter("kaptcha.textproducer.font.size", fsize);// 字体大小
		servletRegistrationBean.addInitParameter("kaptcha.noise.color", nColor);// 干扰线的颜色
		servletRegistrationBean.addInitParameter("kaptcha.textproducer.char.length", clength);// 字符个数
		servletRegistrationBean.addInitParameter("kaptcha.textproducer.font.names", fnames);// 字体
		return servletRegistrationBean;
	}
	
	/**
	 * 配置拦截器,这个方法会覆盖父类的方法
	 */
	public void addInterceptors(InterceptorRegistry registry) {
		String interceptorPath = "/shop/**";
		// 注册一个拦截器,该拦截器用于登录校验(如果没有登录则不允许访问shopadmin下的任何页面)
		InterceptorRegistration loginIR = registry.addInterceptor(new ShopLoginInterceptor());
		// 添加拦截路径
		loginIR.addPathPatterns(interceptorPath);
		// 添加一些不需要进行拦截的路径
		String[] loginExcludePatterns = new String[] { "/shop/ownerlogin", "/shop/ownerlogincheck", "/shop/logout",
				"/shop/register" };
		loginIR.excludePathPatterns(loginExcludePatterns);

		// 注册校验是否对该店铺有操作权限的拦截器
		InterceptorRegistration permissionIR = registry.addInterceptor(new ShopPermissionInterceptor());
		// 排除掉的一些拦截路径的一个集合
		String[] permissionExcludePatterns = new String[] { "/shop/ownerlogin", "/shop/ownerlogincheck",
				"/shop/register", "/shopadmin/shoplist", "/shopadmin/getshoplist", "/shop/logout", "/shop/changepsw",
				"/shop/changelocalpwd", "/shop/ownerbind", "/shop/bindlocalauth", "/shopadmin/shopmanagement",
				"/shopadmin/getshopmanagementinfo", "/shopadmin/shopoperation", "/shop/getshopbyid",
				"/shopadmin/getshopinitinfo", "/shopadmin/registershop" };
		permissionIR.addPathPatterns(interceptorPath);
		permissionIR.excludePathPatterns(permissionExcludePatterns);
	}
}
