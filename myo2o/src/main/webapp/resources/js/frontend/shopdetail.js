$(function() {// shopdetail.js和shoplist.js差不多,都实现了无极滚动,所以要注释请看shoplist.js页面
	var loading = false;
	var maxItems = 20;
	var pageSize = 3;

	// 获取商品列表的url
	var listUrl = '/myo2o/frontend/listproductsbyshop';
	// 默认的页码
	var pageNum = 1;
	// 从地址栏中取出shopId
	var shopId = getQueryString('shopId');
	var productCategoryId = '';
	var productName = '';

	// 获取店铺信息以及店铺商品分类列表的url
	var searchDivUrl = '/myo2o/frontend/listshopdetailpageinfo?shopId='
			+ shopId;

	// 点击兑换奖品按钮触发的事件
	$('exchangelist')
			.attr("href", "/myo2o/frontend/awardlist?shopId=" + shopId);

	// 获取店铺信息以及店铺商品分类列表
	function getSearchDivData() {
		var url = searchDivUrl;
		$
				.getJSON(
						url,
						function(data) {
							if (data.success) {
								var shop = data.shop;
								$('#shop-cover-pic').attr('src',
										getContextPath() + shop.shopImg);
								$('#shop-update-time').html(
										new Date(shop.lastEditTime)
												.Format("yyyy-MM-dd"));
								$('#shop-name').html(shop.shopName);
								$('#shop-desc').html(shop.shopDesc);
								$('#shop-addr').html(shop.shopAddr);
								$('#shop-phone').html(shop.phone);

								var productCategoryList = data.productCategoryList;
								var html = '';
								productCategoryList
										.map(function(item, index) {
											html += '<a href="#" class="button" data-product-search-id='
													+ item.productCategoryId
													+ '>'
													+ item.productCategoryName
													+ '</a>';
										});
								$('#shopdetail-button-div').html(html);
							}
						});
	}
	getSearchDivData();

	// 获取商品列表
	function addItems(pageSize, pageIndex) {
		// 拼接查询参数(注意最后面加了一个shopId,因为我们只要当前店铺的商品)
		var url = listUrl + '?' + 'pageIndex=' + pageIndex + '&pageSize='
				+ pageSize + '&productCategoryId=' + productCategoryId
				+ '&productName=' + productName + '&shopId=' + shopId;
		loading = true;
		$.getJSON(url, function(data) {
			if (data.success) {
				maxItems = data.count;
				var html = '';
				data.productList.map(function(item, index) {
					html += '' + '<div class="card" data-product-id='
							+ item.productId + '>'
							+ '<div class="card-header">' + item.productName
							+ '</div>' + '<div class="card-content">'
							+ '<div class="list-block media-list">' + '<ul>'
							+ '<li class="item-content">'
							+ '<div class="item-media">' + '<img src="'
							+ getContextPath() + item.imgAddr + '" width="44">'
							+ '</div>' + '<div class="item-inner">'
							+ '<div class="item-subtitle">' + item.productDesc
							+ '</div>' + '</div>' + '</li>' + '</ul>'
							+ '</div>' + '</div>' + '<div class="card-footer">'
							+ '<p class="color-gray">'
							+ new Date(item.lastEditTime).Format("yyyy-MM-dd")
							+ '更新</p>' + '<span>点击查看</span>' + '</div>'
							+ '</div>';
				});
				$('.list-div').append(html);
				var total = $('.list-div .card').length;
				if (total >= maxItems) {
					// 加载完毕，则注销无限加载事件，以防不必要的加载
					// $.detachInfiniteScroll($('.infinite-scroll'));
					// 删除加载提示符
					// $('.infinite-scroll-preloader').remove();

					// 这里修改了一个小bug,bug在于:当你前端的查询出了所有符合要求的商品或者商铺之后,按照上面图片中的写法,无极滚动提示符会被删除,那么此时你再到前端页面去进行高级查询(比如在搜索框中输入查询条件进行查询)加载结果就不会是完全的(因为无极滚动提示符被删除了)
					$('.infinite-scroll-preloader').hide();// 修复bug,不能删除这个无极滚动提示符.加载完成后应该隐藏无极滚动提示符
				} else {
					$('.infinite-scroll-preloader').show();
				}
				pageNum += 1;
				loading = false;
				// 刷新页面,显示新加载的店铺
				$.refreshScroller();
			}
		});
	}

	// 默认加载pageSize数量的商品
	addItems(pageSize, pageNum);

	// 定义无极滚动触发事件(下滑屏幕触发)
	$(document).on('infinite', '.infinite-scroll-bottom', function() {
		if (loading)
			return;
		addItems(pageSize, pageNum);
	});

	// 定义点击商品分类标签的事件
	$('#shopdetail-button-div').on(
			'click',
			'.button',
			function(e) {
				productCategoryId = e.target.dataset.productSearchId;
				if (productCategoryId) {
					if ($(e.target).hasClass('button-fill')) {
						$(e.target).removeClass('button-fill');
						productCategoryId = '';
					} else {
						$(e.target).addClass('button-fill').siblings()
								.removeClass('button-fill');
					}
					$('.list-div').empty();
					pageNum = 1;
					addItems(pageSize, pageNum);
				}
			});

	// 定义点击商品列表的事件(点击某一个card,就进入对应商品的商品详情页面)
	$('.list-div')
			.on(
					'click',
					'.card',
					function(e) {
						var productId = e.currentTarget.dataset.productId;
						window.location.href = '/myo2o/frontend/productdetail?productId='
								+ productId;
					});

	// 定义在搜索栏中输入内容进行高级查询的事件,之前使用的input事件,但是反应太快了,导致原先的查询结果还没来及被empty,就加在了新结果,所以有时候会看到重复的加载结果
	$('#search').on('change', function(e) {
		productName = e.target.value;
		$('.list-div').empty();
		pageNum = 1;
		addItems(pageSize, pageNum);
	});

	$('#me').click(function() {
		$.openPanel('#panel-left-demo');
	});
	$.init();
});
