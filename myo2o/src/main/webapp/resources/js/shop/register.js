$(function() {
	var registerUrl = '/myo2o/shop/ownerregister';
	$('#submit').click(function() {
		// 创建localAuth和personInfo对象
		var localAuth = {};
		var personInfo = {};
		// 读取前端页面某些标签元素中的值,并将值写入到创建的对象中
		localAuth.userName = $('#userName').val();
		localAuth.password = $('#password').val();
		personInfo.phone = $('#phone').val();
		personInfo.email = $('#email').val();
		personInfo.name = $('#name').val();
		localAuth.personInfo = personInfo;
		// 获取头像
		var thumbnail = $('#small-img')[0].files[0];
		console.log(thumbnail);
		// 创建表单对象
		var formData = new FormData();
		// 往表单对象中追加要传递到后台的数据信息
		formData.append('thumbnail', thumbnail);
		formData.append('localAuthStr', JSON.stringify(localAuth));
		// 获取验证码
		var verifyCodeActual = $('#j_captcha').val();
		// 如果验证码非空则提示要输入验证码
		if (!verifyCodeActual) {
			$.toast('请输入验证码！');
			return;
		}
		// 往表单对象中追加验证码信息
		formData.append("verifyCodeActual", verifyCodeActual);
		// 将添加了所需信息的表单对象传递到后端shop/ownerregister方法
		$.ajax({
			url : registerUrl,
			type : 'POST',
			data : formData,
			contentType : false,
			processData : false,
			cache : false,
			success : function(data) {
				// 如果后端方法的返回对象模型中的success属性为true,则表示注册成功,于是就转到登录页面进行登录(shop/ownerlogin)
				if (data.success) {
					$.toast('提交成功！');
					window.location.href = '/myo2o/shop/ownerlogin';
				} else {
					$.toast('提交失败！');
					$('#captcha_img').click();
				}
			}
		});
	});

	// 返回按钮:返回到用户登录界面
	$('#back').click(function() {
		window.location.href = '/myo2o/shop/ownerlogin';
	});
});
