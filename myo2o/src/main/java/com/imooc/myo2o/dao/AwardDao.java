package com.imooc.myo2o.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.imooc.myo2o.entity.Award;

public interface AwardDao {
	/**
	 * 依据传进来的查询条件分页显示奖品信息列表
	 * 
	 * @param awardCondition
	 * @param rowIndex
	 * @param pageSize
	 * @return
	 */
	List<Award> queryAwardList(@Param("awardCondition") Award awardCondition, @Param("rowIndex") int rowIndex,
			@Param("pageSize") int pageSize);

	/**
	 * 配合queryAwardList.用于查询奖品总数
	 * 
	 * @param awardCondition
	 * @return
	 */
	int queryAwardCount(@Param("awardCondition") Award awardCondition);

	/**
	 * 根据传入的awardId去查询奖品信息
	 * 
	 * @param awardId
	 * @return
	 */
	Award queryAwardByAwardId(long awardId);

	/**
	 * 添加奖品信息
	 * 
	 * @param award
	 * @return
	 */
	int insertAward(Award award);

	/**
	 * 更新奖品信息
	 * 
	 * @param award
	 * @return
	 */
	int updateAward(Award award);

	/**
	 * 删除奖品信息(其实这里应该多传入一个shopId参数,用来进行验证)
	 * 
	 * @param awardId
	 * @return
	 */
	int deleteAward(@Param("awardId") long awardId, @Param("shopId") long shopId);
}
