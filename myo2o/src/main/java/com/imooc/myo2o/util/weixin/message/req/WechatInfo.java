package com.imooc.myo2o.util.weixin.message.req;

/**
 * 用于接收平台二维码的信息
 * 
 * @author hh
 *
 */
public class WechatInfo {
	private Long customerId;
	private Long productId;
	private Long userAwardId;
	private Long createTime;

	public Long getCustomerId() {
		return customerId;
	}

	public void setCustomerId(Long customerId) {
		this.customerId = customerId;
	}

	public Long getProductId() {
		return productId;
	}

	public void setProductId(Long productId) {
		this.productId = productId;
	}

	public Long getUserAwardId() {
		return userAwardId;
	}

	public void setUserAwardId(Long userAwardId) {
		this.userAwardId = userAwardId;
	}

	public Long getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Long createTime) {
		this.createTime = createTime;
	}

}
