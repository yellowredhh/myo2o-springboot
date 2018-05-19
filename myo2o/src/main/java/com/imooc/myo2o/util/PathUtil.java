package com.imooc.myo2o.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PathUtil {
	// 在ssm框架中硬编码的路径全部移到了application.properties文件中.
	private static String separator = System.getProperty("file.separator");

	private static String winPath;

	@Value("${win.base.path}")
	public void setWinPath(String winPath) {
		PathUtil.winPath = winPath;
	}

	private static String linuxPath;

	@Value("${linux.base.path}")
	public void setLinuxPath(String linuxPath) {
		PathUtil.linuxPath = linuxPath;
	}

	private static String shopImagePath;

	@Value("${shop.relevant.path}")
	public void setShopImagePath(String shopImagePath) {
		PathUtil.shopImagePath = shopImagePath;
	}

	private static String headLineImagePath;

	@Value("${headline.relevant.path}")
	public void setHeadLineImagePath(String headLineImagePath) {
		PathUtil.headLineImagePath = headLineImagePath;
	}

	private static String shopCategoryImagePath;

	@Value("${shopcategory.relevant.path}")
	public void setShopCategoryImagePath(String shopCategoryImagePath) {
		PathUtil.shopCategoryImagePath = shopCategoryImagePath;
	}

	private static String personInfoImagePath;

	@Value("${personinfo.relevant.path}")
	public void setPersonInfoImagePath(String personInfoImagePath) {
		PathUtil.personInfoImagePath = personInfoImagePath;
	}

	/*
	 * 根据系统类型获取图片的绝对路径,不要把这个路径设置在classpath路径下,因为项目重新部署的时候会清空classpath文件夹下的其它文件
	 * 实际生成环境中一般都是把这个目录部署在其他服务器上 windows下的文件夹分隔符是"\",linux和mac是"/";
	 */
	public static String getImgBasePath() {
		String osName = System.getProperty("os.name");
		String basePath = "";
		if (osName.toLowerCase().startsWith("win")) {
			basePath = winPath;
		} else {
			basePath = linuxPath;
		}
		return basePath.replace("/", separator);
	}

	/*
	 * 获取商铺图片的相对路径(这个路径和basePath一起组成了全路径)
	 */
	public static String getShopImagePath(Long shopId) {
		// 其实当我这个相对路径和上面方法中的绝对路径进行组合的时候会多出来一个"/"符号,但是现在没有报错了.通过日志可以看到自动去掉了重复的"/".
		String imagePath = shopImagePath + shopId + separator;
		return imagePath.replace("/", separator);
	}

	public static String getHeadLineImagePath() {
		String imagePath = headLineImagePath;
		return imagePath.replace("/", separator);
	}

	public static String getShopCategoryImagePath() {
		String imagePath = shopCategoryImagePath;
		return imagePath.replace("/", separator);
	}

	public static String getPersonInfoImagePath() {
		String imagePath = personInfoImagePath;
		return imagePath.replace("/", separator);
	}

}
