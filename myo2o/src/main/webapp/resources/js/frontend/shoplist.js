$(function() {
	// 定义一个loading(后面有处理逻辑),用于判定当前页面是否正在加载,如果正在加载则不去做重复加载
	var loading = false;
	// 定义允许返回的最大条数,如果当前所有返回的条数大于这个值,则禁止继续访问后台(这里只是设定一个默认值,实际查询过程中会根据查询结果做动态调整)
	var maxItems = 999;
	// 一页显示的最大数目
	var pageSize = 3;
	// 获取指定条件下的shop的url
	var listUrl = '/myo2o/frontend/listshops';
	// 获取商品的一级店铺列表(如果url中没有parentId则或者二级店铺列表,以及区域信息列表的url
	var searchDivUrl = '/myo2o/frontend/listshopspageinfo';
	// 页码
	var pageNum = 1;
	// 尝试从url中获取parentId(如果是从首页的全部类别点击进这个页面就不带有parentId,如果是从一级店铺点击进来就会带有parentId)
	var parentId = getQueryString('parentId');
	var areaId = '';
	var shopCategoryId = '';
	var shopName = '';

	// 渲染出一级店铺列表和区域列表
	function getSearchDivData() {
		// 如果有parentId,就查找这个一级店铺下面的所有的二级店铺
		// 如果没有parentId(也就是通过点击全部商店进入shoplist界面,则查询出所有的一级店铺
		var url = searchDivUrl + '?' + 'parentId=' + parentId;
		$
				.getJSON(
						url,
						function(data) {
							if (data.success) {
								// 获取后台返回的shopCategoryList
								var shopCategoryList = data.shopCategoryList;
								var html = '';
								html += '<a href="#" class="button" data-category-id=""> 全部类别  </a>';
								// 遍历店铺类别列表,拼接出a标签集
								shopCategoryList
										.map(function(item, index) {
											html += '<a href="#" class="button" data-category-id='
													+ item.shopCategoryId
													+ '>'
													+ item.shopCategoryName
													+ '</a>';
										});
								// 将拼接好的a标签集嵌入到前端页面的id为shoplist-search-div的元素中.
								$('#shoplist-search-div').html(html);
								var selectOptions = '<option value="">全部街道</option>';
								// 获取后台返回的区域列表信息
								var areaList = data.areaList;
								// 遍历区域列表信息,并将其拼接成option标签集
								areaList.map(function(item, index) {
									selectOptions += '<option value="'
											+ item.areaId + '">'
											+ item.areaName + '</option>';
								});
								// 将拼接好的option标签集添加(完全替换)到id为area-search的前端html控件中
								$('#area-search').html(selectOptions);
							}
						});
	}

	// 进入页面时自动调用渲染出一级店铺列表和区域列表的方法
	getSearchDivData();

	// 获取指定查询条件的店铺列表的方法(分页查询和高级查询)
	function addItems(pageSize, pageIndex) {
		// 拼接查询条件(如果没有值就默认不限制该条件,如果有值则按该条件去查询)
		var url = listUrl + '?' + 'pageIndex=' + pageIndex + '&pageSize='
				+ pageSize + '&parentId=' + parentId + '&areaId=' + areaId
				+ '&shopCategoryId=' + shopCategoryId + '&shopName=' + shopName;
		// 将加载状态设置为true,避免重复加载(避免重复查询)
		loading = true;
		// 根据url去后台进行查询,查询结果封装在data中
		$.getJSON(url, function(data) {
			if (data.success) {// 如果从后端查询成功
				// 提取出查询结果的结果条数赋值给maxItems(这个默认是999,现在此处做动态调整)
				maxItems = data.count;
				var html = '';
				// 遍历查询出的店铺列表,拼接出展示条目(card信息)
				data.shopList.map(function(item, index) {
					html += '' + '<div class="card" data-shop-id="'
							+ item.shopId + '">' + '<div class="card-header">'
							+ item.shopName + '</div>'
							+ '<div class="card-content">'
							+ '<div class="list-block media-list">' + '<ul>'
							+ '<li class="item-content">'
							+ '<div class="item-media">' + '<img src="'
							+ getContextPath() + item.shopImg + '" width="44">'
							+ '</div>' + '<div class="item-inner">'
							+ '<div class="item-subtitle">' + item.shopDesc
							+ '</div>' + '</div>' + '</li>' + '</ul>'
							+ '</div>' + '</div>' + '<div class="card-footer">'
							+ '<p class="color-gray">'
							+ new Date(item.lastEditTime).Format("yyyy-MM-dd") // 这个方法在common.js中
							+ '更新</p>' + '<span>点击查看</span>' + '</div>'
							+ '</div>';
				});
				// 将拼接结果追加到前端html中class为list-div的元素中(.append是追加操作,.html是完全替换操作)
				$('.list-div').append(html);
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

	// 点击店铺的卡片进入该店铺的详情页(这个点击事件是可以点击到动态添加的成员的)
	$('.shop-list').on('click', '.card', function(e) {
		var shopId = e.currentTarget.dataset.shopId;
		window.location.href = '/myo2o/frontend/shopdetail?shopId=' + shopId;
	});

	// 选择新的店铺类别之后,就重置页码,清空原先的查询结果,重新去后台按照新的类别进行查询
	$('#shoplist-search-div').on(
			'click',
			'.button',
			function(e) {
				if (parentId) {// 如果传递过来的是一个父类下的子类
					shopCategoryId = e.target.dataset.categoryId;
					// 如果某一个按钮被选中就高亮它,没有被选中就不高亮
					if ($(e.target).hasClass('button-fill')) {
						$(e.target).removeClass('button-fill');
						shopCategoryId = '';
					} else {
						$(e.target).addClass('button-fill').siblings()
								.removeClass('button-fill');
					}
					// remove() - 删除被选元素（及其子元素）
					// empty() - 从被选元素中删除子元素
					// empty相当于只是清空被选元素里面的内容,而remove则连被选元素也要清除掉
					$('.list-div').empty();
					// 重置页码
					pageNum = 1;
					addItems(pageSize, pageNum);
				} else {// 如果传递过来的父类为空，则按照父类查询
					parentId = e.target.dataset.categoryId;
					if ($(e.target).hasClass('button-fill')) {
						$(e.target).removeClass('button-fill');
						parentId = '';
					} else {
						$(e.target).addClass('button-fill').siblings()
								.removeClass('button-fill');
					}
					$('.list-div').empty();
					pageNum = 1;
					addItems(pageSize, pageNum);
					parentId = '';
				}

			});

	// 需要查询的店铺名字发生变化后,重置页码,重新按照新的名字去后台查询,之前使用的input事件,但是反应太快了,导致原先的查询结果还没来及被empty,就加在了新结果,所以有时候会看到重复的加载结果
	$('#search').on('change', function(e) {
		// 将当前标签的value赋值给shopName作为模糊名查询条件
		shopName = e.target.value;
		// 清空原先的查询结果
		$('.list-div').empty();
		// 页码置1
		pageNum = 1;
		// 重新查询
		addItems(pageSize, pageNum);
	});

	// 区域改变之后,重新查询
	$('#area-search').on('change', function() {
		areaId = $('#area-search').val();
		$('.list-div').empty();
		pageNum = 1;
		addItems(pageSize, pageNum);
	});

	// 点击id为"me"的元素之后打开侧边栏
	$('#me').click(function() {
		$.openPanel('#panel-left-demo');
	});

	// 初始化页面
	$.init();
});
