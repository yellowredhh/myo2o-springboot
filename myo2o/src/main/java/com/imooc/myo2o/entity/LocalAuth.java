package com.imooc.myo2o.entity;
/*
 * 本地账号
 */
import java.util.Date;

public class LocalAuth {
	// 主键,用户名,密码
	private Long localAuthId;
	private String userName;
	private String password;
	// 用户id,创建时间,最后一次更新时间
	private Long userId;
	private Date createTime;
	private Date lastEditTime;
	
	// 个人信息,一一对应的关系
	private PersonInfo personInfo;
	
	public Long getLocalAuthId() {
		return localAuthId;
	}
	
	public void setLocalAuthId(Long localAuthId) {
		this.localAuthId = localAuthId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Date getLastEditTime() {
		return lastEditTime;
	}

	public void setLastEditTime(Date lastEditTime) {
		this.lastEditTime = lastEditTime;
	}

	public PersonInfo getPersonInfo() {
		return personInfo;
	}

	public void setPersonInfo(PersonInfo personInfo) {
		this.personInfo = personInfo;
	}

}
