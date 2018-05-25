$(function() {
	// 从url中获取awardId
	var awardId = getQueryString('awardId');
	// 根据awardId获取某一个具体的奖品信息的url
	var infoUrl = '/myo2o/shop/getawardbyid?awardId=' + awardId;
	// 编辑奖品信息的url
	var awardPostUrl = '/myo2o/shop/modifyaward';
	// 由于编辑和添加使用的是同一个页面,所以要设置一个flag来区分
	var isEdit = false;
	if (awardId) {
		// 如果进入这个页面的时候能从url中拿到awardId,则表示就是编辑操作,从后台查询出这个award,并把isEdit设置为true
		getInfo(awardId);
		isEdit = true;
	} else {
		// 没有awardId则表示是添加奖品操作
		awardPostUrl = '/myo2o/shop/addaward';
	}

	$("#pass-date").calendar({
		value : [ '2017-12-31' ]
	});

	// 根据awardId获取award
	function getInfo(id) {
		// 从后台获取award信息
		$.getJSON(infoUrl, function(data) {
			// 如果后台方法没问题
			if (data.success) {
				// 新建一个award JSON对象,并将后台信息赋给这个JSON变量
				var award = data.award;
				// 将查询到的各个信息都填充到前端html对应的标签中
				$('#award-name').val(award.awardName);
				$('#priority').val(award.priority);
				$('#award-desc').val(award.awardDesc);
				$('#point').val(award.point);
			}
		});
	}

	// 编辑或者添加完成之后点击提交时的点击事件
	$('#submit').click(function() {
		var award = {};
		award.awardName = $('#award-name').val();
		award.priority = $('#priority').val();
		award.awardDesc = $('#award-desc').val();
		award.point = $('#point').val();
		award.awardId = awardId ? awardId : '';
		award.expireTime = $('#pass-date').val();
		console.log(award.expireTime);
		// 获取前端html中的文件流
		var thumbnail = $('#small-img')[0].files[0];
		var formData = new FormData();
		formData.append('thumbnail', thumbnail);
		formData.append('awardStr', JSON.stringify(award));
		var verifyCodeActual = $('#j_captcha').val();
		if (!verifyCodeActual) {
			$.toast('请输入验证码！');
			return;
		}
		// 往表单对象中追加验证码
		formData.append("verifyCodeActual", verifyCodeActual);
		$.ajax({
			url : awardPostUrl,
			type : 'POST',
			data : formData,
			contentType : false,
			processData : false,
			cache : false,
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