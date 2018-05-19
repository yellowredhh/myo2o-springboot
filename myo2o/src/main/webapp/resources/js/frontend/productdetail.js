$(function() {
	var productId = getQueryString('productId');
	// 获取商品信息的url
	var productUrl = '/myo2o/frontend/listproductdetailpageinfo?productId='
			+ productId;

	$
			.getJSON(
					productUrl,
					function(data) {
						if (data.success) {
							// 获取商品信息
							var product = data.product;
							// 给商品信息相关html控件赋值
							$('#product-img').attr('src',
									getContextPath() + product.imgAddr);
							$('#product-time').text(
									new Date(product.lastEditTime)
											.Format("yyyy-MM-dd"));
							$('#product-name').text(product.productName);
							$('#product-desc').text(product.productDesc);
							if (product.point != undefined) {
								$('#product-point').text(
										'购买可以获得' + product.point + '积分');
							}
							var imgListHtml = '';
							product.productImgList.map(function(item, index) {
								imgListHtml += '<div> <img src="'
										+ getContextPath() + item.imgAddr
										+ '"/></div>';
							});
							// 生成购买商品的二维码供商家扫描
							imgListHtml += '<div> <img src="/myo2o/frontend/generateqrcode4product?productId='
									+ product.productId + '"/></div>';
							$('#imgList').html(imgListHtml);
						}
					});
	$('#me').click(function() {
		$.openPanel('#panel-left-demo');
	});
	$.init();
});
