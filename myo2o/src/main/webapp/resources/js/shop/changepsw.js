$(function() {
	//更改密码的url
	var url = '/myo2o/shop/changelocalpwd';
	$('#submit').click(function() {
		//获取账号名,原密码
		var userName = $('#userName').val();
		var password = $('#password').val();
		//新密码
		var newPassword = $('#newPassword').val();
		//新建一个表单数据
		var formData = new FormData();
		//往表单数据中添加用户名
		formData.append('userName', userName);
		//往表单数据中添加密码
		formData.append('password', password);
		//往表单数据中添加新密码
		formData.append('newPassword', newPassword);
		//获取前端页面中输入的验证码
		var verifyCodeActual = $('#j_captcha').val();
		//如果没有输入验证码则弹出提示并且返回
		if (!verifyCodeActual) {
			$.toast('请输入验证码！');
			return;
		}
		//往表单数据中添加验证码
		formData.append("verifyCodeActual", verifyCodeActual);
		$.ajax({
			url : url,
			type : 'POST',
			data : formData,
			contentType : false,
			processData : false,
			cache : false,
			success : function(data) {
				//如果更改密码成功,则跳转到商品展示界面
				if (data.success) {
					$.toast('提交成功！');
					window.location.href = '/myo2o/shop/shoplist';
				} else {
					$.toast('提交失败！');
					$('#captcha_img').click();
				}
			}
		});
	});
	
	//如果用户又不想改密码了,则可以点击返回按钮,该按钮可以回到商品列表界面
	$('#back').click(function() {
		window.location.href = '/myo2o/shop/shoplist';
	});
});
