package com.imooc.myo2o.service;

import com.imooc.myo2o.dto.ImageHolder;
import com.imooc.myo2o.dto.LocalAuthExecution;
import com.imooc.myo2o.entity.LocalAuth;

public interface LocalAuthService {
	/**
	 * 根据用户名和密码获取本地账号
	 * @param userName
	 * @return
	 */
	LocalAuth getLocalAuthByUserNameAndPwd(String userName, String password);

	/**
	 * 根据用户id获取本地账号
	 * @param userId
	 * @return
	 */
	LocalAuth getLocalAuthByUserId(long userId);

	/**
	 * 添加本地账号(包括本地账号信息和头像)
	 * @param localAuth
	 * @param profileImg
	 * @return
	 * @throws RuntimeException
	 */
	LocalAuthExecution register(LocalAuth localAuth, ImageHolder imageHolder) throws RuntimeException;

	/**
	 * 绑定微信,生成平台专属账号
	 * @param localAuth
	 * @return
	 * @throws RuntimeException
	 */
	LocalAuthExecution bindLocalAuth(LocalAuth localAuth) throws RuntimeException;

	/**
	 * 修改密码
	 * @param localAuthId
	 * @param userName
	 * @param password
	 * @param newPassword
	 * @param lastEditTime
	 * @return
	 */
	LocalAuthExecution modifyLocalAuth(Long userId, String userName, String password, String newPassword);
}
