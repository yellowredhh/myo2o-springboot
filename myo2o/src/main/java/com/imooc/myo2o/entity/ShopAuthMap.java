package com.imooc.myo2o.entity;

import java.util.Date;

/**
 * 店铺授权
 * @author hh
 *
 */
public class ShopAuthMap {
	//主键
	private Long shopAuthId;
	//雇员id,店铺id(两者组成唯一key约束)
	private Long employeeId;
	private Long shopId;

	private String name;
	//职称名(店员还是经理之类的)
	private String title;
	//职称符号(用于做权限控制)
	private Integer titleFlag;
	//授权有效状态,0,无效,1,有效
	private Integer enableStatus;
	//创建时间
	private Date createTime;
	//最近一次更新时间
	private Date lastEditTime;
	//雇员和店铺实体类
	private PersonInfo employee;
	private Shop shop;

	public Long getShopAuthId() {
		return shopAuthId;
	}

	public void setShopAuthId(Long shopAuthId) {
		this.shopAuthId = shopAuthId;
	}

	public Long getEmployeeId() {
		return employeeId;
	}

	public void setEmployeeId(Long employeeId) {
		this.employeeId = employeeId;
	}

	public Long getShopId() {
		return shopId;
	}

	public void setShopId(Long shopId) {
		this.shopId = shopId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getTitleFlag() {
		return titleFlag;
	}

	public void setTitleFlag(Integer titleFlag) {
		this.titleFlag = titleFlag;
	}

	public Integer getEnableStatus() {
		return enableStatus;
	}

	public void setEnableStatus(Integer enableStatus) {
		this.enableStatus = enableStatus;
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

	public PersonInfo getEmployee() {
		return employee;
	}

	public void setEmployee(PersonInfo employee) {
		this.employee = employee;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public String toString() {
		return "[shopId=" + shopId + ", employeeId=" + employeeId + ", employeeName=" + name + "]";
	}
}
