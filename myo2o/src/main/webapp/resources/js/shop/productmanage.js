$(function() {
	var shopId = 1;
	// 获取此店铺的商品列表的url:这个url所指向的后台方法会把基于同样的查询条件下的商品列表和商品数量都查询出来(调用了两个底层dao方法)
	var listUrl = '/myo2o/shopadmin/getproductlistbyshop?pageIndex=1&pageSize=9999&shopId='
			+ shopId;
	//更改商品上\下架的url
	var deleteUrl = '/myo2o/shopadmin/modifyproduct';

	function getList() {
		// 从后台获取此商铺的商品列表
		$.getJSON(listUrl, function(data) {
			if (data.success) {
				var productList = data.productList;
				var tempHtml = '';
				// 遍历每条商品信息,拼接成一行显示.列信息包括:
				// 商品名称,优先级,上架\下架(含productId),编辑按钮(含productId),预览按钮(含productId)
				productList.map(function(item, index) {
					var textOp = "下架";
					var contraryStatus = 0;
					if (item.enableStatus == 0) {
						// 若状态值为0,表明是已下架的商品,则操作变为上架(即点击上架按钮上架商品),上架和下架都是用的同一个按钮.点击一次变化状态.
						textOp = "上架";
						contraryStatus = 1;
					} else {
						contraryStatus = 0;
					}
					// 拼接每件商品的行信息
					tempHtml += '' + '<div class="row row-product">'
							+ '<div class="col-30">'
							+ item.productName
							+ '</div>'
							+ '<div class="col-20">'
							+ item.point
							+ '</div>'
							+ '<div class="col-50">'
							+ '<a href="#" class="edit" data-id="'
							+ item.productId
							+ '" data-status="'
							+ item.enableStatus
							+ '">编辑</a>'
							+ '<a href="#" class="delete" data-id="'
							+ item.productId
							+ '" data-status="'
							+ contraryStatus
							+ '">'
							+ textOp
							+ '</a>'
							+ '<a href="#" class="preview" data-id="'
							+ item.productId
							+ '" data-status="'
							+ item.enableStatus
							+ '">预览</a>'
							+ '</div>'
							+ '</div>';
				});
				// 将拼接好的信息放入到class为product-wrap的元素控件中.
				$('.product-wrap').html(tempHtml);
			}
		});
	}

	getList();

	//点击上\下架时调用的函数(这个函数也会调用后台的modifyProduct方法,只是这里设置了statusChange为true,所以不需要验证码
	function deleteItem(id, enableStatus) {
		//定义product JSON并设置productId以及状态(上\下架).
		var product = {};
		product.productId = id;
		product.enableStatus = enableStatus;
		$.confirm('确定么?', function() {
			//上下架相关商品
			$.ajax({
				url : deleteUrl,
				type : 'POST',
				data : {
					productStr : JSON.stringify(product),
					//这个statusChange已经在Controller层开了一个口子,所以后台可以拿到的.
					statusChange : true
				},
				dataType : 'json',
				success : function(data) {
					if (data.success) {
						$.toast('操作成功！');
						getList();
					} else {
						$.toast('操作失败！');
					}
				}
			});
		});
	}

	// 将class为product-wrap里面的a标签绑定点击事件.
	$('.product-wrap')
			.on(
					'click',
					'a',
					function(e) {
						// event.currentTarget 属性是在事件冒泡阶段内的当前 DOM 元素，通常等于 this。
						var target = $(e.currentTarget);
						if (target.hasClass('edit')) {
							// 如果有class edit,则点击就进入店铺信息编辑界面,并带有productId参数
							window.location.href = '/myo2o/shopadmin/productoperation?productId='
									+ e.currentTarget.dataset.id;
						} else if (target.hasClass('delete')) {
							// 如果有class
							// delete,则点击就调用后台上\下架商品的功能,改变该商品的状态.并带有productId
							deleteItem(e.currentTarget.dataset.id,
									e.currentTarget.dataset.status);
						} else if (target.hasClass('preview')) {
							// 如果有class preview,则去前台展示系统该商品详情页预览商品情况.
							window.location.href = '/myo2o/frontend/productdetail?productId='
									+ e.currentTarget.dataset.id;
						}
					});

	$('#new').click(function() {
		window.location.href = '/myo2o/shopadmin/productoperation';
	});
});