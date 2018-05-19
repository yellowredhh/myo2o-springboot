$(function() {
	//登录验证的Controller_url
	var loginUrl = '/myo2o/shop/ownerlogincheck';
	//登录次数,累计登录失败3次之后自动弹出验证码要求输入
	var loginCount = 0;

	$('#submit').click(function() {
		//获取账号名字,密码,验证码
		var userName = $('#username').val();
		var password = $('#psw').val();
		var verifyCodeActual = $('#j_captcha').val();
		//是否需要验证码验证,默认为false(不需要)
		var needVerify = false;
		//如果登录失败次数大于3次就要求输入验证码(也就是密码输入错误3次以上加入验证码)
		if (loginCount >= 3) {
			if (!verifyCodeActual) {
				$.toast('请输入验证码！');
				return;
			} else {
				//需要验证码校验
				needVerify = true;
			}
		}
		//前往登录
		$.ajax({
			url : loginUrl,
			async : false,
			cache : false,
			type : "post",
			dataType : 'json',
			data : {
				userName : userName,
				password : password,
				verifyCodeActual : verifyCodeActual,
				needVerify : needVerify
			},
			success : function(data) {
				if (data.success) {
					$.toast('登录成功！');
					window.location.href = '/myo2o/shop/shoplist';
				} else {
					$.toast('登录失败！');
					//登录失败,则累计登录次数加1.
					loginCount++;
					if (loginCount >= 3) {
						//这个验证码模块是之前就已经写在了页面中的,只有当登录账号输入错误3次才会显示出来
						$('#verifyPart').show();
					}
				}
			}
		});
	});
	
	//注册帐号方法
	$('#register').click(function() {
		window.location.href = '/myo2o/shop/register';
	});
});