package com.imooc.myo2o.service;

import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.imooc.myo2o.Exceptions.WechatAuthExecutionException;
import com.imooc.myo2o.dto.ImageHolder;
import com.imooc.myo2o.dto.WechatAuthExecution;
import com.imooc.myo2o.entity.WechatAuth;

public interface WechatAuthService {

	/**
	 * 通过openId查找平台对应的微信账号
	 * @param openId
	 * @return
	 */
	WechatAuth getWechatAuthByOpenId(String openId);

	/**
	 * 注册本平台的微信账号
	 * @param wechatAuth 这个参数中不仅包括了微信账号信息,还包括了用户信息
	 * @param profileImg
	 * @return
	 * @throws RuntimeException
	 */
	WechatAuthExecution register(WechatAuth wechatAuth, ImageHolder imageHolder) throws WechatAuthExecutionException;

}
