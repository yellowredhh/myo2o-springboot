$(function() {
	// 定义访问后台,获取头条列表和一级店铺类别列表的url
	var url = '/myo2o/frontend/listmainpageinfo';
	// 访问后台,获取头条列表和一级店铺类别列表,data就是返回的数据
	$.getJSON(url, function(data) {
		if (data.success) {
			// 拿到返回数据的头条列表
			var headLineList = data.headLineList;
			var swiperHtml = '';
			// 遍历头条列表,并拼接出轮播图组(这里的getContextPath()方法定义在common.js中,用于添加一个图片的myo2o前缀
			headLineList.map(function(item, index) {
				swiperHtml += '' + '<div class="swiper-slide img-wrap">'
						+ '<img class="banner-img" src="' + getContextPath()
						+ item.lineImg + '" alt="' + item.lineName + '">'
						+ '</div>';
			});
			// 将轮播图组赋值给前端控件,JQuery类选择器
			$('.swiper-wrapper').html(swiperHtml);
			// 设定轮播图轮换时间为3秒
			$(".swiper-container").swiper({
				autoplay : 3000,
				// 用户对轮播图进行操作时,是否自动停止autoplay(把鼠标移动到图片上时是否停止自动播放)
				autoplayDisableOnInteraction : false
			});
			// 获取后台传递过来的一级店铺类别列表
			var shopCategoryList = data.shopCategoryList;
			var categoryHtml = '';
			// 遍历一级店铺类别列表,拼接出俩俩(col-50,各占比50%)一行的类别
			shopCategoryList.map(function(item, index) {
				categoryHtml += ''
						+ '<div class="col-50 shop-classify" data-category='
						+ item.shopCategoryId + '>' + '<div class="word">'
						+ '<p class="shop-title">' + item.shopCategoryName
						+ '</p>' + '<p class="shop-desc">'
						+ item.shopCategoryDesc + '</p>' + '</div>'
						+ '<div class="shop-classify-img-warp">'
						+ '<img class="shop-img" src="' + getContextPath()
						+ item.shopCategoryImg + '">' + '</div>' + '</div>';
			});
			$('.row').html(categoryHtml);
		}
	});

	// 点击打开侧栏
	$('#me').click(function() {
		$.openPanel('#panel-left-demo');
	});

	// 点击一级店铺类别时触发的事件
	$('.row').on('click', '.shop-classify', function(e) {
		var shopCategoryId = e.currentTarget.dataset.category;
		var newUrl = '/myo2o/frontend/shoplist?parentId=' + shopCategoryId;
		window.location.href = newUrl;
	});

});
