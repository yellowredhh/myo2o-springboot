$(function() {
	var userName = '';
	// 获取该店铺用户积分的url
	function getList() {
		var listUrl = '/myo2o/shop/listusershopmapsbyshop?pageIndex=1&pageSize=9999&userName='
				+ userName;
		$.getJSON(listUrl, function(data) {
			if (data.success) {
				var userShopMapList = data.userShopMapList;
				var tempHtml = '';
				// 拼接成展示列表
				userShopMapList.map(function(item, index) {
					tempHtml += '' + '<div class="row row-usershopcheck">'
							+ '<div class="col-50">' + item.userName + '</div>'
							+ '<div class="col-50">' + item.point + '</div>'
							+ '</div>';
				});
				// 将拼接结果进行填充
				$('.usershopcheck-wrap').html(tempHtml);
			}
		});
	}

	// 搜索绑定,获取并按照用户名模糊查询
	$('#search').on('change', function(e) {
		userName = e.target.value;
		$('.usershopcheck-wrap').empty();
		getList();
	});

	getList();
});