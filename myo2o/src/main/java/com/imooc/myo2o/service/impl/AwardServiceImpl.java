package com.imooc.myo2o.service.impl;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.imooc.myo2o.dao.AwardDao;
import com.imooc.myo2o.dto.AwardExecution;
import com.imooc.myo2o.dto.ImageHolder;
import com.imooc.myo2o.entity.Award;
import com.imooc.myo2o.entity.ShopCategory;
import com.imooc.myo2o.enums.AwardStateEnum;
import com.imooc.myo2o.service.AwardService;
import com.imooc.myo2o.util.ImageUtils;
import com.imooc.myo2o.util.PageCalculator;
import com.imooc.myo2o.util.PathUtil;

@Service
public class AwardServiceImpl implements AwardService {

	@Autowired
	private AwardDao awardDao;

	@Override
	public AwardExecution getAwardList(Award awardCondition, int pageIndex, int pageSize) {
		// 页转行
		int rowIndex = PageCalculator.calculateRowIndex(pageIndex, pageSize);
		// 根据查询条件返回奖品列表
		List<Award> awardList = awardDao.queryAwardList(awardCondition, rowIndex, pageSize);
		// 基于同样的查询条件查询结果总数
		int count = awardDao.queryAwardCount(awardCondition);
		// 拼接两个查询结果
		AwardExecution ae = new AwardExecution();
		ae.setAwardList(awardList);
		ae.setCount(count);
		return ae;
	}

	@Override
	public Award getAwardById(long awardId) {
		return awardDao.queryAwardByAwardId(awardId);
	}

	@Override
	@Transactional
	public AwardExecution addAward(Award award, ImageHolder imageHolder) {
		if (award != null && award.getShopId() != null) {
			// 设置默认信息
			award.setCreateTime(new Date());
			award.setLastEditTime(new Date());
			// 默认可用
			award.setEnableStatus(1);
			if (imageHolder != null) {
				// 如果传入了奖品图片则添加图片,获取相对路径
				addThumbnail(award, imageHolder);
			}
			try {
				// 插入奖品信息
				int effectedNum = awardDao.insertAward(award);
				if (effectedNum <= 0) {
					throw new RuntimeException("创建商品失败");
				}
			} catch (Exception e) {
				throw new RuntimeException("创建商品失败:" + e.toString());
			}
			return new AwardExecution(AwardStateEnum.SUCCESS, award);
		} else {
			return new AwardExecution(AwardStateEnum.EMPTY);
		}
	}

	@Override
	@Transactional
	public AwardExecution modifyAward(Award award, ImageHolder imageHolder) {
		if (award != null && award.getShopId() != null) {
			award.setLastEditTime(new Date());
			if (imageHolder != null) { // 如果要修改奖品的图片
				// 先查询出奖品的原图片地址
				Award tempAward = awardDao.queryAwardByAwardId(award.getAwardId());
				if (tempAward.getAwardImg() != null) { // 如果奖品之前存在图片信息,则进行删除
					ImageUtils.deleteFileOrPath(tempAward.getAwardImg());
				}
				// 添加新传入的图片
				addThumbnail(award, imageHolder);
			}
			try {
				// 更新奖品信息
				int effectedNum = awardDao.updateAward(award);
				if (effectedNum <= 0) {
					throw new RuntimeException("更新商品信息失败");
				}
				return new AwardExecution(AwardStateEnum.SUCCESS, award);
			} catch (Exception e) {
				throw new RuntimeException("更新商品信息失败:" + e.toString());
			}
		} else {
			return new AwardExecution(AwardStateEnum.EMPTY);
		}
	}

	private void addThumbnail(Award award, ImageHolder iamgeHolder) {
		String dest = PathUtil.getShopCategoryImagePath();
		String thumbnailAddr = ImageUtils.generateNormalImg(iamgeHolder, dest);
		award.setAwardImg(thumbnailAddr);
	}

}
