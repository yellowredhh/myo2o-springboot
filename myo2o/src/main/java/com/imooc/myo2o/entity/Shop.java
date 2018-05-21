package com.imooc.myo2o.entity;

import java.util.Date;
import java.util.List;

/*
 * 店铺
 */
public class Shop {
	//主键
	private Long shopId;
	//店铺拥有着id,店铺类别(属于哪一个一级店铺),店铺名称
	private Long ownerId;
	private Long shopCategoryId;
	private String shopName;
	//店铺描述信息,店铺地址,店铺电话
	private String shopDesc;
	private String shopAddr;
	private String phone;
	//店铺缩略图,店铺经度,纬度
	private String shopImg;
	private Double longitude;
	private Double latitude;
	//权重,创建时间,最后一次更新时间
	private Integer priority;
	private Date createTime;
	private Date lastEditTime;
	//店铺可用状态,建议
	private Integer enableStatus;
	private String advice;
	
	//店铺职员列表,区域,店铺类别,店铺父类别
	private List<ShopAuthMap> staffList;
	private Area area;
	private ShopCategory shopCategory;
	private ShopCategory parentCategory;

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public Long getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Long ownerId) {
		this.ownerId = ownerId;
	}

	public Long getShopCategoryId() {
		return shopCategoryId;
	}

	public void setShopCategoryId(Long shopCategoryId) {
		this.shopCategoryId = shopCategoryId;
	}

	public String getShopName() {
		return shopName;
	}

	public void setShopName(String shopName) {
		this.shopName = shopName;
	}

	public String getShopDesc() {
		return shopDesc;
	}

	public void setShopDesc(String shopDesc) {
		this.shopDesc = shopDesc;
	}

	public String getShopAddr() {
		return shopAddr;
	}

	public void setShopAddr(String shopAddr) {
		this.shopAddr = shopAddr;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getShopImg() {
		return shopImg;
	}

	public void setShopImg(String shopImg) {
		this.shopImg = shopImg;
	}

	public Double getLongitude() {
		return longitude;
	}

	public void setLongitude(Double longitude) {
		this.longitude = longitude;
	}

	public Double getLatitude() {
		return latitude;
	}

	public void setLatitude(Double latitude) {
		this.latitude = latitude;
	}

	public Integer getPriority() {
		return priority;
	}

	public void setPriority(Integer priority) {
		this.priority = priority;
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

	public Integer getEnableStatus() {
		return enableStatus;
	}

	public void setEnableStatus(Integer enableStatus) {
		this.enableStatus = enableStatus;
	}

	public List<ShopAuthMap> getStaffList() {
		return staffList;
	}

	public void setStaffList(List<ShopAuthMap> staffList) {
		this.staffList = staffList;
	}

	public Area getArea() {
		return area;
	}

	public void setArea(Area area) {
		this.area = area;
	}

	public ShopCategory getShopCategory() {
		return shopCategory;
	}

	public void setShopCategory(ShopCategory shopCategory) {
		this.shopCategory = shopCategory;
	}

	public String getAdvice() {
		return advice;
	}

	public void setAdvice(String advice) {
		this.advice = advice;
	}

	public String toString() {
		return "[shopId=" + shopId + ", shopName=" + shopName + "]";
	}

	public ShopCategory getParentCategory() {
		return parentCategory;
	}

	public void setParentCategory(ShopCategory parentCategory) {
		this.parentCategory = parentCategory;
	}

}
