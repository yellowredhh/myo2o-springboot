package com.imooc.myo2o.service.impl;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.imooc.myo2o.dao.LocalAuthDao;
import com.imooc.myo2o.dao.PersonInfoDao;
import com.imooc.myo2o.dto.ImageHolder;
import com.imooc.myo2o.dto.LocalAuthExecution;
import com.imooc.myo2o.entity.LocalAuth;
import com.imooc.myo2o.entity.PersonInfo;
import com.imooc.myo2o.enums.LocalAuthStateEnum;
import com.imooc.myo2o.service.LocalAuthService;
import com.imooc.myo2o.util.ImageUtils;
import com.imooc.myo2o.util.MD5;
import com.imooc.myo2o.util.PathUtil;

@Service
public class LocalAuthServiceImpl implements LocalAuthService {

	@Autowired
	private LocalAuthDao localAuthDao;
	@Autowired
	private PersonInfoDao personInfoDao;

	/**
	 * 根据用户名和密码获取本地账号
	 */
	@Override
	public LocalAuth getLocalAuthByUserNameAndPwd(String userName, String password) {
		return localAuthDao.queryLocalByUserNameAndPwd(userName, password);
	}

	/**
	 * 根据userId获取本地账号
	 */
	@Override
	public LocalAuth getLocalAuthByUserId(long userId) {
		return localAuthDao.queryLocalByUserId(userId);
	}

	/**
	 * 注册本地账号(传入本地账号信息和头像)
	 */
	@Override
	@Transactional
	public LocalAuthExecution register(LocalAuth localAuth, ImageHolder imageHolder) throws RuntimeException {
		//空值判定(必须要有用户信息才能注册,也就是密码和用户名都不能为空,用户名是唯一约束)
		if (localAuth == null || localAuth.getPassword() == null || localAuth.getUserName() == null) {
			return new LocalAuthExecution(LocalAuthStateEnum.NULL_AUTH_INFO);
		}
		try {
			//设置默认信息
			localAuth.setCreateTime(new Date());
			localAuth.setLastEditTime(new Date());
			//调用MD5工具对密码进行加密
			localAuth.setPassword(MD5.getMd5(localAuth.getPassword()));
			//如果用户信息不为空并且userId不为空
			if (localAuth.getPersonInfo() != null && localAuth.getPersonInfo().getUserId() == null) {
				if (imageHolder != null) {
					//如果传入了头像
					localAuth.getPersonInfo().setCreateTime(new Date());
					localAuth.getPersonInfo().setLastEditTime(new Date());
					localAuth.getPersonInfo().setEnableStatus(1);
					try {
						addProfileImg(localAuth, imageHolder);
					} catch (Exception e) {
						throw new RuntimeException("addUserProfileImg error: " + e.getMessage());
					}
				}
				try {
					PersonInfo personInfo = localAuth.getPersonInfo();
					//添加本地账号
					int effectedNum = personInfoDao.insertPersonInfo(personInfo);
					localAuth.setUserId(personInfo.getUserId());
					if (effectedNum <= 0) {
						throw new RuntimeException("添加用户信息失败");
					}
				} catch (Exception e) {
					throw new RuntimeException("insertPersonInfo error: " + e.getMessage());
				}
			}
			int effectedNum = localAuthDao.insertLocalAuth(localAuth);
			if (effectedNum <= 0) {
				throw new RuntimeException("帐号创建失败");
			} else {
				return new LocalAuthExecution(LocalAuthStateEnum.SUCCESS, localAuth);
			}
		} catch (Exception e) {
			throw new RuntimeException("insertLocalAuth error: " + e.getMessage());
		}
	}

	/**
	 * 绑定微信账号的方法
	 */
	@Override
	@Transactional
	public LocalAuthExecution bindLocalAuth(LocalAuth localAuth) throws RuntimeException {
		//空值判定,用户信息不为空才继续
		if (localAuth == null || localAuth.getPassword() == null || localAuth.getUserName() == null
				|| localAuth.getUserId() == null) {
			return new LocalAuthExecution(LocalAuthStateEnum.NULL_AUTH_INFO);
		}
		//根据传入的用户信息获取本地账号
		LocalAuth tempAuth = localAuthDao.queryLocalByUserId(localAuth.getUserId());
		//如果已经绑定过了,则直接返回,避免重复绑定
		if (tempAuth != null) {
			return new LocalAuthExecution(LocalAuthStateEnum.ONLY_ONE_ACCOUNT);
		}
		try {
			localAuth.setCreateTime(new Date());
			localAuth.setLastEditTime(new Date());
			//对密码进行MD5加密
			localAuth.setPassword(MD5.getMd5(localAuth.getPassword()));
			int effectedNum = localAuthDao.insertLocalAuth(localAuth);
			//判定绑定是否成功
			if (effectedNum <= 0) {
				throw new RuntimeException("帐号绑定失败");
			} else {
				return new LocalAuthExecution(LocalAuthStateEnum.SUCCESS, localAuth);
			}
		} catch (Exception e) {
			throw new RuntimeException("insertLocalAuth error: " + e.getMessage());
		}
	}

	@Override
	@Transactional
	public LocalAuthExecution modifyLocalAuth(Long userId, String userName, String password, String newPassword) {
		//判断userId是否为空,新旧密码是否相同,如果不满足条件则返回错误
		if (userId != null && userName != null && password != null && newPassword != null
				&& !password.equals(newPassword)) {
			try {
				//对新密码进行MD5加密
				int effectedNum = localAuthDao.updateLocalAuth(userId, userName, MD5.getMd5(password),
						MD5.getMd5(newPassword), new Date());
				if (effectedNum <= 0) {
					throw new RuntimeException("更新密码失败");
				}
				return new LocalAuthExecution(LocalAuthStateEnum.SUCCESS);
			} catch (Exception e) {
				throw new RuntimeException("更新密码失败:" + e.toString());
			}
		} else {
			return new LocalAuthExecution(LocalAuthStateEnum.NULL_AUTH_INFO);
		}
	}

	/**
	 * 添加头像的方法
	 * @param localAuth
	 * @param imageHolder
	 */
	private void addProfileImg(LocalAuth localAuth, ImageHolder imageHolder) {
		String dest = PathUtil.getPersonInfoImagePath();
		String profileImgAddr = ImageUtils.generateThumbnail(imageHolder, dest);
		localAuth.getPersonInfo().setProfileImg(profileImgAddr);
	}

}
