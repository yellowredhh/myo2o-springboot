package com.imooc.myo2o.util;

import java.security.Key;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

/**
 * 对称加密算法(加密和解密所用到的钥匙是相同的)
 * 非对称加密算法(分公私钥,公钥和私钥)
 * 这里使用的DES算法是java自带的
 * @author hh
 *
 */
public class DESUtils {

	private static Key key;
	//设置密钥key
	private static String KEY_STR = "myKey";
	//设置编码
	private static String CHARSETNAME = "UTF-8";
	//设置算法
	private static String ALGORITHM = "DES";

	//静态代码块生成DES算法实例
	static {
		try {
			//生成DES算法对象
			KeyGenerator generator = KeyGenerator.getInstance(ALGORITHM);
			//运用SHA1安全策略
			SecureRandom secureRandom = SecureRandom.getInstance("SHA1PRNG");
			//设置上密钥种子
			secureRandom.setSeed(KEY_STR.getBytes());
			//初始化基于SHA1的算法对象
			generator.init(secureRandom);
			//生成密钥对象
			key = generator.generateKey();
			generator = null;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取加密后的信息
	 * @param str
	 * @return
	 */
	public static String getEncryptString(String str) {
		//基于BASE64进行编码,接收byte[]并转化为String(这里是encoder)
		BASE64Encoder base64encoder = new BASE64Encoder();
		try {
			//按照utf8编码
			byte[] bytes = str.getBytes(CHARSETNAME);
			//获取加密对象
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			//初始化加密信息
			cipher.init(Cipher.ENCRYPT_MODE, key);
			//加密
			byte[] doFinal = cipher.doFinal(bytes);
			//byte[]to encode好的String并返回
			return base64encoder.encode(doFinal);
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}

	/**
	 * 获取解密之后的信息
	 * @param str
	 * @return
	 */
	public static String getDecryptString(String str) {
		//基于BASE64进行编码,接收byte[]并转化为String(这里是decoder)
		BASE64Decoder base64decoder = new BASE64Decoder();
		try {
			//将字符串decode成byte[]
			byte[] bytes = base64decoder.decodeBuffer(str);
			//获取解密对象
			Cipher cipher = Cipher.getInstance(ALGORITHM);
			//初始化解密信息
			cipher.init(Cipher.DECRYPT_MODE, key);
			//解密
			byte[] doFinal = cipher.doFinal(bytes);
			//返回解密结果
			return new String(doFinal, CHARSETNAME);
		} catch (Exception e) {
			// TODO: handle exception
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] args) {
		//加密结果(mysql数据库的账号密码(包括本地mysql密码和远程服务器的mysql密码),微信测试号appID和appsecret
		System.out.println(getEncryptString("root"));
		System.out.println(getEncryptString("work"));
		System.out.println(getEncryptString("test"));
		System.out.println(getEncryptString("yellow20red20"));
		System.out.println(getEncryptString("Yellow20red20!"));
		System.out.println(getEncryptString("wx68941ffdb197fb96"));
		System.out.println(getEncryptString("2bb4fe0205408d49f8ef1400d12ace17"));
		System.out.println("===================");
		//解密结果
		System.out.println(getDecryptString("WnplV/ietfQ="));
		System.out.println(getDecryptString("zCKAAEaFQUI="));
		System.out.println(getDecryptString("LSVgCQvPKkM="));
		System.out.println(getDecryptString("iHAQCL0wuq1j9YIbhYDigw=="));
		System.out.println(getDecryptString("YipjoG5yuOBlJp9V99MP4Q=="));
		System.out.println(getDecryptString("j46pWKtsZAGv2RuVnb64sbMopWbYQeA/"));
		System.out.println(getDecryptString("64bv6mWjj75pLGid9gQKyhGw+9rYHOcO2VDwDnYszBYfJAfVsP+M2w=="));

		System.out.println(getEncryptString("wxd7f6c5b8899fba83"));
		System.out.println(getEncryptString("665ae80dba31fc91ab6191e7da4d676d"));
	}

}