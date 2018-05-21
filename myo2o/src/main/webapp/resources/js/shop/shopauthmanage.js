$(function() {
	// 列出该店铺下面所有的授权信息的URL(这里不需要传入shopId,在url所指的方法中会从session的currentShop中获取shopId
	var listUrl = '/myo2o/shop/listshopauthmapsbyshop?pageIndex=1&pageSize=9999';
	// 修改授权信息的URL
	var deleteUrl = '/myo2o/shop/removeshopauthmap';

	function getList() {
		$.getJSON(listUrl, function(data) {
			if (data.success) {
				var shopauthList = data.shopAuthMapList;
				var tempHtml = '';
				shopauthList.map(function(item, index) {
					tempHtml += '' + '<div class="row row-shopauth">'
							+ '<div class="col-40">'
							+ item.name
							+ '</div>'
							+ '<div class="col-20">'
							+ item.title
							+ '</div>'
							+ '<div class="col-40">'
							+ '<a href="#" class="edit" data-employee-id="'
							+ item.employeeId
							+ '" data-auth-id="'
							+ item.shopAuthId
							+ '" data-status="'
							+ item.enableStatus
							+ '">编辑</a>'
							+ '<a href="#" class="delete" data-employee-id="'
							+ item.employeeId
							+ '" data-auth-id="'
							+ item.shopAuthId
							+ '" data-status="'
							+ item.enableStatus
							+ '">删除</a>'
							+ '</div>'
							+ '</div>';
				});
				$('.shopauth-wrap').html(tempHtml);
			}
		});
	}

	getList();

	function deleteItem(id) {
		$.confirm('确定么?', function() {
			$.ajax({
				url : deleteUrl,
				type : 'POST',
				data : {
					shopAuthId : id,
				},
				dataType : 'json',
				success : function(data) {
					if (data.success) {
						$.toast('删除成功！');
						// 删除授权信息成功了则重新刷新一下目前的授权列表信息
						getList();
					} else {
						$.toast('删除失败！');
					}
				}
			});
		});
	}

	// 点击class元素中带有shopauth-wrap字段的元素下面的a标签时触发的操作:跳转到shopauthedit方法.
	$('.shopauth-wrap')
			.on(
					'click',
					'a',
					function(e) {
						var target = $(e.currentTarget);
						// 如果a标签的class信息中带有edit字段则表示进行编辑,跳转到编辑url(携带有shopauthId)
						if (target.hasClass('edit')) {
							window.location.href = '/myo2o/shop/shopauthedit?shopauthId='
									+ e.currentTarget.dataset.authId;
							// 如果a标签的class信息中带有delete字段,则表示进行删除,使用本js中的删除方法
						} else if (target.hasClass('delete')) {
							deleteItem(e.currentTarget.dataset.authId);
						}
					});

	// $('#new').click(function () {
	// window.location.href = '/myo2o/shop/shopauthedit';
	// });
});