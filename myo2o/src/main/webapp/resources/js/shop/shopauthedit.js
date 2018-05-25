$(function() {
	// 这个shopauthedit页面时从shopauthmanage中的编辑按钮跳转过来的(这个跳转的时候会带有shopAuthId)
	var shopAuthId = getQueryString('shopAuthId');
	var infoUrl = '/myo2o/shop/getshopauthmapbyid?shopAuthId=' + shopAuthId;

	// 编辑完后更新编辑信息的url
	var shopAuthPostUrl = '/myo2o/shop/modifyshopauthmap';

	// 判断从shopauthmanage界面跳转过来的时候是否带有shopAuthId,如果带有则跳转到getInfo方法
	if (shopAuthId) {
		getInfo(shopAuthId);
	} else {
		$.toast('用户不存在！');
		// 如果没有shopAuthId,则报错,提示'用户不存在',并且跳转会店铺管理界面
		window.location.href = '/myo2o/shop/shopmanage';
	}

	function getInfo(id) {
		// 访问后台的shop/getshopauthmapbyid方法获取到当前要编辑的授权信息的相关信息(就两个,一个是雇员,一个是职位)
		$.getJSON(infoUrl, function(data) {
			if (data.success) {
				var shopAuthMap = data.shopAuthMap;
				$('#shopauth-name').val(shopAuthMap.name);
				$('#title').val(shopAuthMap.title);
			}
		});
	}

	// 编辑完之后要更新shopAuthMap,所以要调用modifyshopauthmap方法
	$('#submit').click(function() {
		var shopAuth = {};
		// 获取编辑之后的新的雇员名字和职位
		shopAuth.name = $('#shopauth-name').val();
		shopAuth.title = $('#title').val();
		shopAuth.shopAuthId = shopAuthId;
		var verifyCodeActual = $('#j_captcha').val();
		if (!verifyCodeActual) {
			$.toast('请输入验证码！');
			return;
		}
		$.ajax({
			url : shopAuthPostUrl,
			type : 'POST',
			contentType : "application/x-www-form-urlencoded; charset=utf-8",
			data : {
				// 将JSON参数转化为字符串
				shopAuthMapStr : JSON.stringify(shopAuth),
				verifyCodeActual : verifyCodeActual
			},
			success : function(data) {
				if (data.success) {
					$.toast('提交成功！');
					$('#captcha_img').click();
				} else {
					$.toast('提交失败！');
					$('#captcha_img').click();
				}
			}
		});
	});
});