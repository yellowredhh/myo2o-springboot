package com.imooc.myo2o.service;

import com.imooc.myo2o.dto.AwardExecution;
import com.imooc.myo2o.dto.ImageHolder;
import com.imooc.myo2o.entity.Award;

public interface AwardService {

	/**
	 * 根据传入的条件分页返回奖品列表,并返回该查询条件下的总数
	 * 
	 * @param awardCondition
	 * @param pageIndex
	 * @param pageSize
	 * @return
	 */
	AwardExecution getAwardList(Award awardCondition, int pageIndex, int pageSize);

	/**
	 * 根据奖品id查询奖品
	 * 
	 * @param awardId
	 * @return
	 */
	Award getAwardById(long awardId);

	/**
	 * 添加奖品信息(包括奖品的图片)
	 * 
	 * @param award
	 * @param thumbnail
	 * @return
	 */
	AwardExecution addAward(Award award, ImageHolder imageHolder);

	/**
	 * 修改奖品(包括更换奖品图片)
	 * 
	 * @param award
	 * @param thumbnail
	 * @param awardImgs
	 * @return
	 */
	AwardExecution modifyAward(Award award, ImageHolder imageHolder);

}
