$(function() {
	getlist();
	function getlist(e) {
		$.ajax({
			url : "/myo2o/shopadmin/getshoplist",
			type : "get",
			dataType : "json",
			success : function(data) {
				if (data.success) {
					handleList(data.shopList);
					handleUser(data.user);
				}
			}
		});
	}
	
	/* function getlist(e)中的e指的是什么?
	 * e指的是事件：在firefox中，只能在事件现场使用window.event，所以只有把event传给函数使用。

	所以，为了兼容FF和其它浏览器，一般会在函数里重新给e赋值：e = window.event || e;

	也就是说，如果window.event存在，则该浏览器支持直接使用window.event，否在就是不支持，不支持就使用传进来的e。*/

	function handleUser(data) {
		$('#user-name').text(data);
	}

	function handleList(data) {
		var html = '';
		data.map(function(item, index) {
			html += '<div class="row row-shop"><div class="col-40">'
					+ item.shopName + '</div><div class="col-40">'
					+ shopStatus(item.enableStatus)
					+ '</div><div class="col-20">'
					+ goShop(item.enableStatus, item.shopId) + '</div></div>';

		});
		$('.shop-wrap').html(html);
	}

	function goShop(status, id) {
		if (status != 0 && status != -1) {
			return '<a href="/myo2o/shopadmin/shopmanage?shopId=' + id
					+ '">进入</a>';
		} else {
			return '';
		}
	}

	function shopStatus(status) {
		if (status == 0) {
			return '审核中';
		} else if (status == -1) {
			return '店铺非法';
		} else {
			return '审核通过';
		}
	}

	$('#log-out').click(function() {
		$.ajax({
			url : "/myo2o/shop/logout",
			type : "post",
			contentType : false,
			processData : false,
			cache : false,
			success : function(data) {
				if (data.success) {
					window.location.href = '/myo2o/shop/ownerlogin';
				}
			},
			error : function(data, error) {
				alert(error);
			}
		});
	});

	//getlist();
});
