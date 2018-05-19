package com.imooc.myo2o.dao;

import com.imooc.myo2o.entity.WechatAuth;

public interface WechatAuthDao {
	/**
	 * 通过openId查询对应本平台的微信账号(openId:当用户关注了微信公众号时会自行创建以作为唯一区分)
	 * @param openId
	 * @return
	 */
	WechatAuth queryWechatInfoByOpenId(String openId);

	/**
	 * 添加对应本平台的微信账号
	 * @param wechatAuth
	 * @return
	 */
	int insertWechatAuth(WechatAuth wechatAuth);

	/**
	 * 
	 * @param wechatAuthId
	 * @return
	 */
	int deleteWechatAuth(Long wechatAuthId);
}
