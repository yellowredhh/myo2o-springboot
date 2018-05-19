package com.imooc.myo2o.service.impl;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.myo2o.Exceptions.WechatAuthExecutionException;
import com.imooc.myo2o.dao.PersonInfoDao;
import com.imooc.myo2o.dao.WechatAuthDao;
import com.imooc.myo2o.dto.ImageHolder;
import com.imooc.myo2o.dto.WechatAuthExecution;
import com.imooc.myo2o.entity.PersonInfo;
import com.imooc.myo2o.entity.WechatAuth;
import com.imooc.myo2o.enums.WechatAuthStateEnum;
import com.imooc.myo2o.service.WechatAuthService;
import com.imooc.myo2o.util.ImageUtils;
import com.imooc.myo2o.util.PathUtil;

@Service
public class WechatAuthServiceImpl implements WechatAuthService {
	private static Logger log = LoggerFactory.getLogger(WechatAuthServiceImpl.class);
	@Autowired
	private WechatAuthDao wechatAuthDao;
	@Autowired
	private PersonInfoDao personInfoDao;

	/**
	 * 根据openId返回带有用户信息的微信信息
	 */
	@Override
	public WechatAuth getWechatAuthByOpenId(String openId) {
		return wechatAuthDao.queryWechatInfoByOpenId(openId);
	}

	/**
	 * 注册微信账号(注册微信账号时会调用dao层的两个:
	 * 包括添加用户信息的personInfoDao.insertPersonInfo(personInfo)
	 * 和注册微信账号wechatAuthDao.insertWechatAuth(wechatAuth))
	 */
	@Override
	@Transactional
	public WechatAuthExecution register(WechatAuth wechatAuth, ImageHolder imageHolder)
			throws WechatAuthExecutionException {
		//空值判定
		if (wechatAuth == null || wechatAuth.getOpenId() == null) {
			return new WechatAuthExecution(WechatAuthStateEnum.NULL_AUTH_INFO);
		}
		try {
			//设置账号创建时间
			wechatAuth.setCreateTime(new Date());
			//如果微信信息里面有用户信息,并且userId为空,则表示该用户第一次通过微信使用平台,需要进行注册.
			if (wechatAuth.getPersonInfo() != null && wechatAuth.getPersonInfo().getUserId() == null) {
				if (imageHolder != null) {//如果含有图片(头像),则进行添加
					try {
						addProfileImg(wechatAuth, imageHolder);
					} catch (Exception e) {
						log.debug("addUserProfileImg error:" + e.toString());
						throw new RuntimeException("addUserProfileImg error: " + e.getMessage());
					}
				}
				try {
					//给微信信息中的用户信息的各项属性设置初始值
					wechatAuth.getPersonInfo().setCreateTime(new Date());
					wechatAuth.getPersonInfo().setLastEditTime(new Date());
					wechatAuth.getPersonInfo().setCustomerFlag(1);
					wechatAuth.getPersonInfo().setShopOwnerFlag(1);
					wechatAuth.getPersonInfo().setAdminFlag(0);
					wechatAuth.getPersonInfo().setEnableStatus(1);
					PersonInfo personInfo = wechatAuth.getPersonInfo();
					//添加用户信息(userId是PersonInfo的主键,所以插入成功时会自动生成userId)
					int effectedNum = personInfoDao.insertPersonInfo(personInfo);
					//将用户信息中的userId设置到微信信息中
					wechatAuth.setUserId(personInfo.getUserId());
					if (effectedNum <= 0) {
						throw new WechatAuthExecutionException("添加用户信息失败");
					}
				} catch (Exception e) {
					log.debug("insertPersonInfo error:" + e.toString());
					throw new WechatAuthExecutionException("insertPersonInfo error: " + e.getMessage());
				}
			}
			//创建专属于本平台的微信账号
			int effectedNum = wechatAuthDao.insertWechatAuth(wechatAuth);
			if (effectedNum <= 0) {
				throw new WechatAuthExecutionException("帐号创建失败");
			} else {
				return new WechatAuthExecution(WechatAuthStateEnum.SUCCESS, wechatAuth);
			}
		} catch (Exception e) {
			log.debug("insertWechatAuth error:" + e.toString());
			throw new WechatAuthExecutionException("insertWechatAuth error: " + e.getMessage());
		}
	}

	/**
	 * 添加微信头像
	 * @param wechatAuth
	 * @param imageHolder
	 */
	private void addProfileImg(WechatAuth wechatAuth, ImageHolder imageHolder) {
		String dest = PathUtil.getPersonInfoImagePath();
		String profileImgAddr = ImageUtils.generateThumbnail(imageHolder, dest);
		wechatAuth.getPersonInfo().setProfileImg(profileImgAddr);
	}
}
