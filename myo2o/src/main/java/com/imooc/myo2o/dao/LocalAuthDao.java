package com.imooc.myo2o.dao;

import java.util.Date;

import org.apache.ibatis.annotations.Param;

import com.imooc.myo2o.entity.LocalAuth;

public interface LocalAuthDao {

	/**
	 * 通过账号和密码查询对应信息,登录用
	 * @param userName
	 * @param password
	 * @return
	 */
	LocalAuth queryLocalByUserNameAndPwd(@Param("userName") String userName,
			@Param("password") String password);

	/**
	 * 通过用户id查询对应的localAuth
	 * @param userId
	 * @return
	 */
	LocalAuth queryLocalByUserId(@Param("userId") long userId);

	/**
	 * 添加平台账号
	 * @param localAuth
	 * @return
	 */
	int insertLocalAuth(LocalAuth localAuth);

	/**
	 * 通过userId,userName,password(原密码)来更改密码
	 * @param localAuth
	 * @return
	 */
	int updateLocalAuth(@Param("userId") Long userId,
			@Param("userName") String userName,
			@Param("password") String password,
			@Param("newPassword") String newPassword,
			@Param("lastEditTime") Date lastEditTime);
}
