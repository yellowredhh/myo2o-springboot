package com.imooc.myo2o.dto;

import java.util.List;

import com.imooc.myo2o.entity.Shop;
import com.imooc.myo2o.enums.ShopStateEnum;

public class ShopExecution {

	// 结果状态
	private int state;

	// 状态标识
	private String stateInfo;

	// 店铺数量
	private int count;

	// 操作的商铺(增删改店铺的时候使用)
	private Shop shop;

	// 商铺列表(查询店铺列表的时候使用)
	private List<Shop> shopList;

	public ShopExecution() {
	}

	// 操作失败得时候使用的构造器
	public ShopExecution(ShopStateEnum shopStateEnum) {
		this.state = shopStateEnum.getState();
		this.stateInfo = shopStateEnum.getStateInfo();
	}

	// 操作成功的时候使用的构造器
	public ShopExecution(ShopStateEnum shopStateEnum, Shop shop) {
		this.state = shopStateEnum.getState();
		this.stateInfo = shopStateEnum.getStateInfo();
		this.shop = shop;
	}

	// 操作成功的时候使用的构造器
	public ShopExecution(ShopStateEnum shopStateEnum, List<Shop> shopList) {
		this.state = shopStateEnum.getState();
		this.stateInfo = shopStateEnum.getStateInfo();
		this.shopList = shopList;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	public void setStateInfo(String stateInfo) {
		this.stateInfo = stateInfo;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public List<Shop> getShopList() {
		return shopList;
	}

	public void setShopList(List<Shop> shopList) {
		this.shopList = shopList;
	}

}
