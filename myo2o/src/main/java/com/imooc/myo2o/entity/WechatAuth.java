package com.imooc.myo2o.entity;

import java.util.Date;

/*
 * 微信账号
 */
public class WechatAuth {
	//主键
	private Long wechatAuthId;
	//外键:这个和personinfo中的userId关联,表示这个微信账号属于哪一个用户
	private Long userId;
	//微信获取用户信息的凭证,对于某一个微信公众号而言具有唯一性
	private String openId;
	private Date createTime;
	//用户信息
	private PersonInfo personInfo;

	public Long getWechatAuthId() {
		return wechatAuthId;
	}

	public void setWechatAuthId(Long wechatAuthId) {
		this.wechatAuthId = wechatAuthId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getOpenId() {
		return openId;
	}

	public void setOpenId(String openId) {
		this.openId = openId;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public PersonInfo getPersonInfo() {
		return personInfo;
	}

	public void setPersonInfo(PersonInfo personInfo) {
		this.personInfo = personInfo;
	}

}
