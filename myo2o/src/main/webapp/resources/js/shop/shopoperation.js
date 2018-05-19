/**
 * 
 */
$(function() {
	var shopId = getQueryString('shopId');
	// 如果url中带有shopId这个参数,则这次操作是更新商铺,如果没有携带shopId参数,则这次操作是注册商铺.
	var isEdit = shopId ? true : false;
	var initUrl = '/myo2o/shopadmin/getshopinitinfo';
	var registerShopUrl = '/myo2o/shopadmin/registershop';
	//这里有点迷:下面的两个Url在重定向后为什么会自动加上myo2o/shopadmin前缀?
	var shopInfoUrl = 'getshopbyid?shopId=' + shopId;
	var editShopUrl = 'modifyshop';
	// alert(initUrl);
	if (!isEdit) {
		//注册商铺
		getShopInitInfo();
	} else {
		//修改商铺
		getInfo(shopId);
	}

	function getShopInitInfo() {
		$.getJSON(initUrl, function(data) {
			if (data.success) {
				var shopCategory = "";
				var tempAreaHtml = "";
				data.shopCategoryList.map(function(item, index) {
					shopCategory += '<option data-id="' + item.shopCategoryId
							+ '">' + item.shopCategoryName + '</option>';

				});
				data.areaList.map(function(item, index) {
					tempAreaHtml += '<option data-id="' + item.areaId + '">'
							+ item.areaName + '</option>';
				});
				// 把查询出来的shopCategory信息填充到页面元素id为shop-category的元素中
				$('#shop-category').html(shopCategory);
				$('#area').html(tempAreaHtml);
			}
		});
	}

	$('#submit').click(function() {
		var shop = {};
		if (isEdit) {
			shop.shopId = shopId;
		}
		shop.shopName = $('#shop-name').val();
		shop.shopAddr = $('#shop-addr').val();
		shop.phone = $('#shop-phone').val();
		shop.shopDesc = $('#shop-desc').val();

		shop.shopCategory = {
			shopCategoryId : $('#shop-category').find('option').not(function() {
				return !this.selected;
			}).data('id')
		};
		shop.area = {
			areaId : $('#area').find('option').not(function() {
				return !this.selected;
			}).data('id')
		};

		var shopImg = $("#shop-img")[0].files[0];
		var formData = new FormData();
		formData.append('shopImg', shopImg);
		formData.append('shopStr', JSON.stringify(shop));
		var verifyCodeActual = $('#j_captcha').val();
		if (!verifyCodeActual) {
			$.toast('请输入验证码！');
			return;
		}
		formData.append("verifyCodeActual", verifyCodeActual);
		$.ajax({
			url : isEdit ? editShopUrl : registerShopUrl,
			type : 'POST',
			// contentType: "application/x-www-form-urlencoded; charset=utf-8",
			data : formData,
			contentType : false,
			processData : false,
			cache : false,
			success : function(data) {
				if (data.success) {
					$.toast('提交成功！');
				} else {
					$.toast('提交失败！' + data.errMsg);
				}
				$('#captcha_img').click();
			}
		});
	});

	function getInfo(shopId) {
		$.getJSON(shopInfoUrl, function(data) {
			if (data.success) {
				var shop = data.shop;
				$('#shop-name').val(shop.shopName);
				$('#shop-addr').val(shop.shopAddr);
				$('#shop-phone').val(shop.phone);
				$('#shop-desc').val(shop.shopDesc);
				var shopCategory = '<option data-id="'
						+ shop.shopCategory.shopCategoryId + '" selected>'
						+ shop.shopCategory.shopCategoryName + '</option>';
				var tempAreaHtml = '';
				data.areaList.map(function(item, index) {
					tempAreaHtml += '<option data-id="' + item.areaId + '">'
							+ item.areaName + '</option>';
				});
				$('#shop-category').html(shopCategory);
				// 店铺种类在注册完了之后就不允许修改了,所以要加上disabled.
				$('#shop-category').attr('disabled', 'disabled');
				$('#area').html(tempAreaHtml);
				$("#area option[data-id='" + shop.area.areaId + "']").attr(
						'selected', 'selected');
			}
		});
	}
})