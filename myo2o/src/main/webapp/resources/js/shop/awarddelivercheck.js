$(function() {
	var awardName = '';
	// 获取积分兑换记录的url
	function getList() {
		var listUrl = '/myo2o/shop/listuserawardmapsbyshop?pageIndex=1&pageSize=9999&awardName='
				+ awardName;
		$.getJSON(listUrl, function(data) {
			if (data.success) {
				var userAwardMapList = data.userAwardMapList;
				var tempHtml = '';
				// 拼接成展示列表
				userAwardMapList.map(function(item, index) {
					tempHtml += ''
							+ '<div class="row row-awarddeliver">'
							+ '<div class="col-33">'
							+ item.awardName
							+ '</div>'
							+ '<div class="col-33 awarddeliver-time">'
							+ new Date(item.createTime)
									.Format("yyyy-MM-dd HH:mm:ss") + '</div>'
							+ '<div class="col-33">' + item.userName + '</div>'
							+ '</div>';
				});
				// 将拼接的展示列表填充到calss元素为awarddeliver-wrap的标签中
				$('.awarddeliver-wrap').html(tempHtml);
			}
		});
	}

	// 搜索绑定,获取并按照奖品名模糊查询
	$('#search').on('change', function(e) {
		awardName = e.target.value;
		$('.awarddeliver-wrap').empty();
		getList();
	});

	getList();
});