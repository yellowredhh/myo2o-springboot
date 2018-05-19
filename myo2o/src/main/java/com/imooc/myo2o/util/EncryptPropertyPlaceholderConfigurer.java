package com.imooc.myo2o.util;

import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;

/**
 * 其实PropertyPlaceholderConfigurer这个类就是在配置数据库连接的xml文件中的那个property-placeholder属性的父类
 * @author hh
 *
 */
public class EncryptPropertyPlaceholderConfigurer extends PropertyPlaceholderConfigurer {
	//进行过加密的字段的数组
	private String[] encryptPropNames = { "jdbc.username", "jdbc.password" };
	
	/**
	 * 对关键属性进行转换
	 */
	@Override
	protected String convertProperty(String propertyName, String propertyValue) {
		//如果propertyName在进行过加密的字段的数组中,则进行解密
		if (isEncryptProp(propertyName)) {
			//对已经加密的字段进行解密工作
			String decryptValue = DESUtils.getDecryptString(propertyValue);
			return decryptValue;
		} else {
			return propertyValue;
		}
	}
	
	/**
	 * 判断传入的字符是否需要进行解密工作
	 * @param propertyName
	 * @return
	 */
	private boolean isEncryptProp(String propertyName) {
		for (String encryptpropertyName : encryptPropNames) {
			if (encryptpropertyName.equals(propertyName))
				return true;
		}
		return false;
	}
}
