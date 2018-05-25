$(function() {
	// 定义一个loading(后面有处理逻辑),用于判定当前页面是否正在加载,如果正在加载则不去做重复加载
	var loading = false;
	// 定义允许返回的最大条数,如果当前所有返回的条数大于这个值,则禁止继续访问后台(这里只是设定一个默认值,实际查询过程中会根据查询结果做动态调整)
	var maxItems = 999;
	// 一页显示的最大数目
	var pageSize = 3;
	// 获取奖品列表的url
	var listUrl = '/myo2o/shop/listawardsbyshop';
	// 兑换奖品的url(兑换奖品从另一个角度看就是添加一条userAwardMap映射)
	var exchangeUrl = '/myo2o/shop/adduserawardmap';
	// 页码
	var pageNum = 1;
	// 从地址栏URL中获取shopId
	var shopId = getQueryString('shopId');
	var awardName = '';
	var canProceed = false;
	var totalPoint = 0;

	// 获取指定查询条件的奖品列表的方法(分页查询和高级查询)
	function addItems(pageSize, pageIndex) {
		// 拼接查询条件
		var url = listUrl + '?' + 'pageIndex=' + pageIndex + '&pageSize='
				+ pageSize + '&shopId=' + shopId + '&awardName=' + awardName;
		// 将加载状态设置为true,避免重复加载(避免重复查询)
		loading = true;
		// 根据url去后台进行查询,查询结果封装在data中
		$.getJSON(url, function(data) {
			if (data.success) {// 如果从后端查询成功
				// 提取出查询结果的结果条数赋值给maxItems(这个默认是999,现在此处做动态调整)
				maxItems = data.count;
				var html = '';
				// 遍历查询出的列表,拼接出展示条目(card信息)
				data.awardList.map(function(item, index) {
					html += '' + '<div class="card" data-award-id="'
							+ item.awardId + '"data-point="' + item.point
							+ '">' + '<div class="card-header">'
							+ item.awardName + '<span class="pull-right">需要积分'
							+ item.point + '</span></div>'
							+ '<div class="card-content">'
							+ '<div class="list-block media-list">' + '<ul>'
							+ '<li class="item-content">'
							+ '<div class="item-media">' + '<img src="'
							+ getContextPath() + item.awardImg
							+ '" width="44">' + '</div>'
							+ '<div class="item-inner">'
							+ '<div class="item-subtitle">' + item.awardDesc
							+ '</div>' + '</div>' + '</li>' + '</ul>'
							+ '</div>' + '</div>' + '<div class="card-footer">'
							+ '<p class="color-gray">'
							+ new Date(item.lastEditTime).Format("yyyy-MM-dd") // 这个方法在common.js中
							+ '更新</p>';
					// 对总积分进行判断,所以这里不用在for循环中,并且这里用的是data.totalPoint,而不是item.totalPoint
					if (data.totalPoint != undefined) {
						// 若用户有积分,则显示"点击领取"按钮
						html += '<span>点击领取</span>' + '</div>' + '</div>'
					} else {
						html += +'</div>' + '</div>'
					}
				});
				// 将拼接结果追加到前端html中class为list-div的元素中(.append是追加操作,.html是完全替换操作)
				$('.list-div').append(html);
				if (data.totalPoint != undefined) {
					// 若用户在该店铺有积分,则显示积分
					canProceed = true;
					$('#title').text('当前积分' + data.totalPoint);
					totalPoint = data.totalPoint;
				}
				// 获取目前为止的卡片总数,包括之前加载的(就是滚动加载,无极加载,无限加载)
				var total = $('.list-div .card').length;
				if (total >= maxItems) {// 如果当前条目超过了之前查询所获取到的结果条数,则停止无极加载
					// 加载完毕，则注销无限加载事件，以防不必要的加载
					// $.detachInfiniteScroll($('.infinite-scroll'));
					// 删除加载提示符(删除html页面中class元素为infinite-scroll-preloader的控件)
					// $('.infinite-scroll-preloader').remove();

					$('.infinite-scroll-preloader').hide();// 修复bug,不能删除这个无极滚动提示符.加载完成后应该隐藏无极滚动提示符
				} else {
					$('.infinite-scroll-preloader').show();
				}
				// 否则页码加1
				pageNum += 1;
				// 将加载状态设置为false,下次可以继续加载
				loading = false;
				// 刷新页面,显示新加载的店铺
				$.refreshScroller();
			}
		});
	}

	// 预先加载10条
	addItems(pageSize, pageNum);

	// 下滑屏幕自动刷新页面进行无极加载)
	$(document).on('infinite', '.infinite-scroll-bottom', function() {
		if (loading)// 如果当前正在加载,则退出,避免重复加载
			return;
		// 否则就继续加载
		addItems(pageSize, pageNum);
	});

	// 点击奖品的卡片进入该奖品的详情页(这个点击事件是可以点击到动态添加的成员的)
	$('.award-list').on(
			'click',
			'.card',
			function(e) {
				var shopId = e.currentTarget.dataset.awardId;
				window.location.href = '/myo2o/frontend/awarddetail?awardId='
						+ awardId;
			});

	// 点击某一个奖品就前往领取
	$('.award-list').on(
			'click',
			'.card',
			function(e) {
				// 如果顾客在当前店铺有积分,并且积分大于当前选择的奖品所需要的积分
				if (canProceed && totalPoint > e.currentTarget.dataset.point) {
					$.confirm('需要消耗' + e.currentTarget.dataset.point
							+ '积分,确定操作么'), function() {
						// 访问后台,领取奖品
						$.ajax({
							url : exchangeUrl,
							type : 'POST',
							data : {
								awardId : e.currentTarget.dataset.point,
							},
							dataType : 'json',
							success : function(data) {
								if (data.success) {
									$.toast('操作成功！');
									totalPoint = totalPoint
											- e.currentTarget.dataset.point;
									$('#title').text('当前积分' + totalPoint);
								} else {
									$.toast('操作失败！');
								}
							}
						});
					}
				} else {
					$.toast('积分不足或者无权操作!');
				}
			});

	// 需要查询的奖品名字发生变化后,重置页码,重新按照新的名字去后台查询,之前使用的input事件,但是反应太快了,导致原先的查询结果还没来及被empty,就加在了新结果,所以有时候会看到重复的加载结果
	$('#search').on('change', function(e) {
		// 将当前标签的value赋值给awardName作为模糊名查询条件
		awardName = e.target.value;
		// 清空原先的查询结果
		$('.list-div').empty();
		// 页码置1
		pageNum = 1;
		// 重新查询
		addItems(pageSize, pageNum);
	});

	// 点击id为"me"的元素之后打开侧边栏
	$('#me').click(function() {
		$.openPanel('#panel-left-demo');
	});

	// 初始化页面
	$.init();
});
