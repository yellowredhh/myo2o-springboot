$(function() {
	var productId = getQueryString('productId');

	// 这里手动硬编码了一个shopId,如果不这样做,也可以先去访问localhost:8080/myo2o/shopadmin/getshopmanagementinfo?shopId=15,通过这个方法就可以设置一个shopId到session中.
	// var shopId = 15;手动硬编码没用,因为这个shopId要放到session的currentShop中去,所以还是按上面说的做.
	// 对于编辑界面,则使用下面的infoUrl:一打开编辑页面就去后台查询出对应的商品信息填入前端页面.
	var infoUrl = '/myo2o/shopadmin/getproductbyproductid?productId=' + productId;

	// var categoryUrl =
	// '/myo2o/shopadmin/getproductcategorylistbyshopId?shopId='+ shopId;
	// 获取当前店铺的商品类别列表
	var categoryUrl = '/myo2o/shopadmin/getproductcategorylist';

	// 更新商品信息
	var productPostUrl = '/myo2o/shopadmin/modifyproduct';

	// 商品添加和商品编辑使用的是同一个界面,所以使用这个标识符来区别是编辑还是添加
	var isEdit = false;
	if (productId) {
		// 如果url中带有productId,则是编辑.
		getInfo(productId);
		isEdit = true;
	} else {
		// 否则就是添加商品.
		getCategory();
		productPostUrl = '/myo2o/shopadmin/addproduct';
	}

	// 获取需要编辑的商品的商品信息,并赋值给表单.
	function getInfo(id) {
		$
				.getJSON(
						infoUrl,
						function(data) {
							if (data.success) {
								// 从返回的JSON当中获取product信息,并赋值给表单.
								var product = data.product;
								$('#product-name').val(product.productName);
								$('#product-desc').val(product.productDesc);
								$('#priority').val(product.priority);
								$('#point').val(product.point);
								$('#normal-price').val(product.normalPrice);
								$('#promotion-price').val(
										product.promotionPrice);

								var optionHtml = '';
								// 获取所有的商品类别列表
								var optionArr = data.productCategoryList;
								// 获取原本的商品的商品类别.
								var optionSelected = product.productCategory.productCategoryId;
								// 生成前端的HTML商品类别列表,并默认选择编辑前的商品类别
								optionArr
										.map(function(item, index) {
											var isSelect = optionSelected === item.productCategoryId ? 'selected'
													: '';
											optionHtml += '<option data-value="'
													+ item.productCategoryId
													+ '"'
													+ isSelect
													+ '>'
													+ item.productCategoryName
													+ '</option>';
										});
								// 将获取到的所有的商品类别填入id为category的元素当中.
								$('#category').html(optionHtml);
							}
						});
	}

	// 为商品添加操作提供该店铺下的所有的商品类别列表
	function getCategory() {
		$.getJSON(categoryUrl, function(data) {
			if (data.success) {
				var productCategoryList = data.productCategoryList;
				var optionHtml = '';
				productCategoryList.map(function(item, index) {
					optionHtml += '<option data-value="'
							+ item.productCategoryId + '">'
							+ item.productCategoryName + '</option>';
				});
				$('#category').html(optionHtml);
			}
		});
	}
	// 类选择器,选中class元素中含有detail-img-div的元素.
	// 针对商品详情图控件组,若该控件组的最后一个元素(last-child)发生了变化(即添加了要上传的图片)
	// 且文件上传数量还没有达到6个,就添加一个新的文件上传控件.
	$('.detail-img-div').on('change', '.detail-img:last-child', function() {
		if ($('.detail-img').length < 6) {
			// 选中id为detail-img的元素.
			$('#detail-img').append('<input type="file" class="detail-img">');
		}
	});

	$('#submit').click(
			function() {
				// 创建商品的json对象,并从表单中获取对应的值.
				var product = {};
				product.productName = $('#product-name').val();
				product.productDesc = $('#product-desc').val();
				product.priority = $('#priority').val();
				product.point = $('#point').val();
				product.normalPrice = $('#normal-price').val();
				product.promotionPrice = $('#promotion-price').val();
				// 获取选中的商品类别.
				product.productCategory = {
					productCategoryId : $('#category').find('option').not(
							function() {
								return !this.selected;
							}).data('value')
				};
				product.productId = productId;

				// 获取文件流
				var thumbnail = $('#small-img')[0].files[0];
				console.log(thumbnail);
				// 生成表单对象,用于接收参数并且传递给后台.
				var formData = new FormData();
				formData.append('thumbnail', thumbnail);
				// 遍历商品详情图控件,获取里面的文件流.
				$('.detail-img').map(
						function(index, item) {
							// 判断该控件是否已经选择了文件
							if ($('.detail-img')[index].files.length > 0) {
								// 将第i个文件流赋值给key为productImgi的表单键值对里
								formData.append('productImg' + index,
										$('.detail-img')[index].files[0]);
							}
						});
				// 将product json对象转换成字符流保存至表单对象key为productStr的键值对里.
				formData.append('productStr', JSON.stringify(product));
				// 获取表单里输入的验证码
				var verifyCodeActual = $('#j_captcha').val();
				if (!verifyCodeActual) {
					$.toast('请输入验证码！');
					return;
				}
				// 将验证码放入表单对象
				formData.append("verifyCodeActual", verifyCodeActual);
				// 将所有的数据(所有的数据都在formData中了)提交至后台进行处理
				$.ajax({
					url : productPostUrl,
					type : 'POST',
					data : formData,
					contentType : false,
					processData : false,
					cache : false,
					//function(data)表示拿到这个productPostUrl所指向的后台方法的返回值(这里是modifyProduct方法的返回值)
					success : function(data) {
						// 无论提交成功与否都把验证码进行更换.(提交成功则提示"提交成功!")
						if (data.success) {
							$.toast('提交成功！');
							$('#captcha_img').click();
						} else {
							$.toast('提交失败！');
							$('#captcha_img').click();
						}
					}
				});
			});
});