$(function() {
	var userAwardId = getQueryString('userAwardId');
	// 根据userAwardId获取用户奖品映射的URL
	var awardUrl = '/myo2o/frontend/getawardbyuserawardid?userAwardId='
			+ userAwardId;

	$
			.getJSON(
					awardUrl,
					function(data) {
						if (data.success) {
							// 获取商品信息
							var award = data.award;
							// 给商品信息相关html控件赋值
							$('#award-img').attr('src',
									getContextPath() + award.awardImg);
							$('#create-time').text(
									new Date(award.createtime)
											.Format("yyyy-MM-dd"));
							$('#award-name').text(award.awardName);
							$('#award-desc').text(award.awardDesc);
							var imgListHtml = '';
							// 如果还没有兑换过该奖品,则可以生成二维码
							if (data.usedStatus == 0) {
								imgListHtml += '<div> <img src="/myo2o/frontend/generateqrcode4award?userAwardId='
										+ userAwardId
										+ '" width="100%"/></div>';
								$('#imgList').html(imgListHtml);
							}

						}
					});
	$('#me').click(function() {
		$.openPanel('#panel-left-demo');
	});
	$.init();
});
