package com.imooc.myo2o.entity;

import java.util.Date;

public class ProductSellDaily {
	// 哪天的销量,精确到天
	private Date createTime;
	// 销量
	private Integer total;
	// 哪个店铺的销量
	private Shop shop;
	// 哪种产品的销量
	private Product product;

	public Date getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Date createTime) {
		this.createTime = createTime;
	}

	public Integer getTotal() {
		return total;
	}

	public void setTotal(Integer total) {
		this.total = total;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}
}
