$(function() {
	var loading = false;
	var maxItems = 20;
	var pageSize = 10;
	// 获取用户兑换记录的url
	var listUrl = '/myo2o/frontend/listuserawardmapsbycustomer';

	var pageNum = 1;
	var productName = '';
	// 按照查询条件获取奖品兑换记录列表,并生成对应的HTML元素添加到页面中
	function addItems(pageSize, pageIndex) {
		// 生成新条目的HTML
		var url = listUrl + '?pageIndex=' + pageIndex + '&pageSize=' + pageSize
				+ '&awardName=' + awardName;
		loading = true;
		$.getJSON(url, function(data) {
			if (data.success) {
				maxItems = data.count;
				var html = '';
				data.userAwardMapList.map(function(item, index) {
					var status = '';
					// 根据usedStatus判断该奖品有没有被领取过
					if (item.usedStatus == 0) {
						status = "未领取";
					} else if (item.usedStatus == 1) {
						status = "已领取";
					}
					html += '' + '<div class="card" data-award-id='
							+ item.userAwardId + '>'
							+ '<div class="card-header">' + item.shop.shopName
							+ '<span class="pull-right">' + status + '</span>'
							+ '</div>' + '<div class="card-content">'
							+ '<div class="list-block media-list">' + '<ul>'
							+ '<li class="item-content">'
							+ '<div class="item-inner">'
							+ '<div class="item-subtitle">' + item.awardName
							+ '</div>' + '</div>' + '</li>' + '</ul>'
							+ '</div>' + '</div>' + '<div class="card-footer">'
							+ '<p class="color-gray">'
							+ new Date(item.createTime).Format("yyyy-MM-dd")
							+ '</p>' + '<span>消费积分:' + item.point + '</span>'
							+ '</div>' + '</div>';
				});
				$('.list-div').append(html);
				var total = $('.list-div .card').length;
				if (total >= maxItems) {
					// 加载完毕，则注销无限加载事件，以防不必要的加载
					// $.detachInfiniteScroll($('.infinite-scroll'));
					// 删除加载提示符
					// $('.infinite-scroll-preloader').remove();
					$('.infinite-scroll-preloader').hide();// 修复bug,不能删除这个无极滚动提示符.加载完成后应该隐藏无极滚动提示符
				} else {
					$('.infinite-scroll-preloader').show();
				}
				pageNum += 1;
				loading = false;
				$.refreshScroller();
			}
		});
	}

	addItems(pageSize, pageNum);

	// 绑定卡片点击事件,点击卡片可以去查看奖品详情,客户凭借详情页面上的二维码到店中给店员扫描就可以领取奖品
	$('.list-div')
			.on(
					'click',
					'.card',
					function(e) {
						var userAwardId = e.currentTarget.dataset.userAwardId;
						window.location.href = '/myo2o/frontend/awarddetail?userAwardId='
								+ userAwardId;
					});

	// 无极滚动
	$(document).on('infinite', '.infinite-scroll-bottom', function() {
		if (loading)
			return;
		addItems(pageSize, pageNum);
	});

	// 绑定搜索事件,主要是传入奖品名进行模糊查询
	$('#search').on('change', function(e) {
		awardName = e.target.value;
		$('.list-div').empty();
		pageNum = 1;
		addItems(pageSize, pageNum);
	});

	// 侧边栏绑定按钮
	$('#me').click(function() {
		$.openPanel('#panel-left-demo');
	});
	$.init();
});
