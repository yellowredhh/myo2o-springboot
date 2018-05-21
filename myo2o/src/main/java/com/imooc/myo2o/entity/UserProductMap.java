package com.imooc.myo2o.entity;

import java.util.Date;

/**
 * (这UserAwardMap,UserProductMap,UserShopMap都是和积分有关的实体类,第一个用于关联用户和奖品之间的积分机制,比如用户领取这个奖品需要多少积分,则积分要减去对应的值,第二个用于关联用户和商品之间的机制,比如用户买一个商品增加了多少积分,第三个用于关联用户和店铺之间的积分机制,比如某一个用户在某一个店铺有多少积分)
 * 
 * 顾客消费商品的映射
 * 
 * @author hh
 *
 */
public class UserProductMap {
	// 主键id(作为primary key)
	private Long userProductId;
	// 用户id,商品id,店铺id(数据库中分别作为key)
	private Long userId;
	private Long productId;
	private Long shopId;
	// 用户姓名,产品名称,创建时间
	private String userName;
	private String productName;
	private Date createTime;
	// 消费商品所获得的积分
	private Integer point;
	// 三个实体类(外键关联)
	private PersonInfo user;
	private Product product;
	private Shop shop;

	public Long getUserProductId() {
		return userProductId;
	}

	public void setUserProductId(Long userProductId) {
		this.userProductId = userProductId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
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

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
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
