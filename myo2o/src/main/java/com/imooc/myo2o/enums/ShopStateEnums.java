package com.imooc.myo2o.enums;

public enum ShopStateEnums {
	CHECK(0, "审核中"), OFFLINE(-1, "商铺下线"), SUCCESS(1, "成功"), PASS(2, "通过认证"), INNER_ERROR(-1001,
			"系统内部错误"), NULL_SHOPID(-1002, "ShopId为空"), NULL_SHOP_INFO(-1003, "传入了空的信息");

	private int state;
	private String stateInfo;

	private ShopStateEnums(int state, String stateInfo) {
		this.state = state;
		this.stateInfo = stateInfo;
	}

	public int getState() {
		return state;
	}

	public String getStateInfo() {
		return stateInfo;
	}

	/*
	 * 根据传入的int参数返回对应的枚举值.
	 */
	public static ShopStateEnums getEnumsByNumber(int state) {
		for (ShopStateEnums item : values()) {
			if (item.state == state) {
				return item;
			}
		}
		return null;
	}
}
