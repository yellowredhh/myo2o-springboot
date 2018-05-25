$(function() {
	// 获取该店铺下的奖品列表的url
	var listUrl = '/myo2o/shop/listawardsbyshop?pageIndex=1&pageSize=9999';
	// 更新可见状态(在这个页面中只能是上架还是下架状态)奖品的url
	var deleteUrl = '/myo2o/shop/modifyaward';

	function getList() {
		// 访问后台获取奖品列表数据
		$.getJSON(listUrl, function(data) {
			if (data.success) {
				var awardList = data.awardList;
				var tempHtml = '';
				awardList.map(function(item, index) {
					var textOp = "下架";
					var contraryStatus = 0;
					if (item.enableStatus == 0) {
						textOp = "上架";
						contraryStatus = 1;
					} else {
						contraryStatus = 0;
					}
					// 拼接奖品名称,积分,奖品可用状态
					tempHtml += '' + '<div class="row row-award">'
							+ '<div class="col-30">'
							+ item.awardName
							+ '</div>'
							+ '<div class="col-20">'
							+ item.point
							+ '</div>'
							+ '<div class="col-50">'
							+ '<a href="#" class="edit" data-id="'
							+ item.awardId
							+ '" data-status="'
							+ item.enableStatus
							+ '">编辑</a>'
							+ '<a href="#" class="delete" data-id="'
							+ item.awardId
							+ '" data-status="'
							+ contraryStatus
							+ '">'
							+ textOp
							+ '</a>'
							+ '<a href="#" class="preview" data-id="'
							+ item.awardId
							+ '" data-status="'
							+ item.enableStatus
							+ '">预览</a>'
							+ '</div>'
							+ '</div>';
				});
				// 填充拼接的信息
				$('.award-wrap').html(tempHtml);
			}
		});
	}

	getList();

	// 修改奖品可用状态的方法
	function deleteItem(awardId, enableStatus) {
		// 定义award JSON对象并添加awardId以及状态(上/下架)
		var award = {};
		award.awardId = awardId;
		award.enableStatus = enableStatus;
		$.confirm('确定么?', function() {
			// 上下架
			$.ajax({
				url : deleteUrl,
				type : 'POST',
				data : {
					awardStr : JSON.stringify(award),
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
	// 将class为award-wrap里面的a标签绑定点击事件.(就是getList方法中动态添加的编辑,上下架以及预览操作)
	$('.award-wrap')
			.on(
					'click',
					'a',
					function(e) {
						// event.currentTarget 属性是在事件冒泡阶段内的当前 DOM 元素，通常等于 this。
						var target = $(e.currentTarget);
						if (target.hasClass('edit')) {
							// 如果有class edit,则点击就进入奖品信息编辑界面,并带有awardId参数
							window.location.href = '/myo2o/shop/awardedit?awardId='
									+ e.currentTarget.dataset.id;
						} else if (target.hasClass('delete')) {
							// 如果有class
							// delete,则点击就调用后台上\下架奖品的功能,改变该奖品的状态.并带有awardId
							deleteItem(e.currentTarget.dataset.id,
									e.currentTarget.dataset.status);
						} else if (target.hasClass('preview')) {
							// 如果有class preview,则去前台展示系统该奖品详情页预览奖品情况.
							window.location.href = '/myo2o/frontend/awarddetail?awardId='
									+ e.currentTarget.dataset.id;
						}
					});

	// 新增按钮的点击事件
	$('#new').click(function() {
		window.location.href = '/myo2o/shop/awardedit';
	});
});