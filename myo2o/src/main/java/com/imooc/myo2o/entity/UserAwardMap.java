package com.imooc.myo2o.entity;

import java.util.Date;

/**
 * 顾客已经领取的奖品的映射
 * @author hh
 *
 */
public class UserAwardMap {
	//主键id
	private Long userAwardId;
	//用户id,奖品id,店铺id
	private Long userId;
	private Long awardId;
	private Long shopId;
	//用户姓名,奖品名称,过期时间
	private String userName;
	private String awardName;
	private Date expireTime;
	//创建时间,使用状态(0表示未兑换,1表示已经兑换),兑换该奖品所需要的积分
	private Date createTime;
	private Integer usedStatus;
	private Integer point;
	//顾客实体类,奖品实体类,店铺实体类
	private PersonInfo user;
	private Award award;
	private Shop shop;

	public Long getUserAwardId() {
		return userAwardId;
	}

	public void setUserAwardId(Long userAwardId) {
		this.userAwardId = userAwardId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getAwardId() {
		return awardId;
	}

	public void setAwardId(Long awardId) {
		this.awardId = awardId;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getAwardName() {
		return awardName;
	}

	public void setAwardName(String awardName) {
		this.awardName = awardName;
	}

	public Date getExpireTime() {
		return expireTime;
	}

	public void setExpireTime(Date expireTime) {
		this.expireTime = expireTime;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getUsedStatus() {
		return usedStatus;
	}

	public void setUsedStatus(Integer usedStatus) {
		this.usedStatus = usedStatus;
	}

	public Integer getPoint() {
		return point;
	}

	public void setPoint(Integer point) {
		this.point = point;
	}

	public PersonInfo getUser() {
		return user;
	}

	public void setUser(PersonInfo user) {
		this.user = user;
	}

	public Award getAward() {
		return award;
	}

	public void setAward(Award award) {
		this.award = award;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

}
