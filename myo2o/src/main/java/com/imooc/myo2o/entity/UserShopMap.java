package com.imooc.myo2o.entity;

import java.util.Date;

/**
 * (这UserAwardMap,UserProductMap,UserShopMap都是和积分有关的实体类,第一个用于关联用户和奖品之间的积分机制,比如用户领取了这个奖品,则积分要减去对应的值,第二个用于关联用户和商品之间的机制,比如用户买一个商品增加了多少积分,第三个用于关联用户和店铺之间的积分机制,比如某一个用户在某一个店铺有多少积分)
 * 
 * 顾客在某一个店铺消费的总积分的映射
 * 
 * @author hh
 *
 */
public class UserShopMap {
	// 主键id
	private Long userShopId;
	// key(两者一起组成了唯一key约束)
	private Long userId;
	private Long shopId;

	private String userName;
	private String shopName;
	private Date createTime;
	// 顾客在该店铺的总积分
	private Integer point;
	// 三个实体类(顾客,产品,店铺)
	private PersonInfo user;
	private Product product;
	private Shop shop;

	public Long getUserShopId() {
		return userShopId;
	}

	public void setUserShopId(Long userShopId) {
		this.userShopId = userShopId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
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

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
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

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

}
