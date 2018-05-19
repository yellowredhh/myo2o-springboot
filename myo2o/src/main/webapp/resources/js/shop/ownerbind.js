$(function() {
	//绑定账号的url
	var bindUrl = '/myo2o/shop/bindlocalauth';

	$('#submit').click(function() {
		//获取输入的账号
		var userName = $('#username').val();
		//获取密码
		var password = $('#psw').val();
		//获取验证码
		var verifyCodeActual = $('#j_captcha').val();
		var needVerify = false;
		if (!verifyCodeActual) {
			$.toast('请输入验证码！');
			return;
		}
		//访问后台,绑定账号
		$.ajax({
			url : bindUrl,
			async : false,
			cache : false,
			type : "post",
			dataType : 'json',
			data : {
				userName : userName,
				password : password,
				verifyCodeActual : verifyCodeActual
			},
			success : function(data) {
				if (data.success) {
					//绑定成功则跳转到商铺列表界面(因为我开始绑定的时候就是从店铺列表界面进来的)
					$.toast('绑定成功！');
					window.location.href = '/myo2o/shopadmin/shoplist';
				} else {
					$.toast('绑定失败！');
					$('#captcha_img').click();
				}
			}
		});
	});
});