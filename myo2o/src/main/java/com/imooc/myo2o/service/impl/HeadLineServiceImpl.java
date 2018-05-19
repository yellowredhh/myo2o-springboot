package com.imooc.myo2o.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.myo2o.cache.JedisUtil;
import com.imooc.myo2o.dao.HeadLineDao;
import com.imooc.myo2o.dto.HeadLineExecution;
import com.imooc.myo2o.entity.HeadLine;
import com.imooc.myo2o.enums.HeadLineStateEnum;
import com.imooc.myo2o.service.HeadLineService;
import com.imooc.myo2o.util.ImageUtils;

@Service
public class HeadLineServiceImpl implements HeadLineService {
	@Autowired
	private JedisUtil.Strings jedisStrings;
	@Autowired
	private JedisUtil.Keys jedisKeys;
	@Autowired
	private HeadLineDao headLineDao;

	//这个就是作为redis数据库的键值对的键
	private static String HLLISTKEY = "headlinelist";

	@Override
	public List<HeadLine> getHeadLineList(HeadLine headLineCondition) throws IOException {
		//定义接受对象
		List<HeadLine> headLineList = null;
		//定义jackson数据转换操作类
		ObjectMapper mapper = new ObjectMapper();
		//定义redis的key前缀
		String key = HLLISTKEY;
		//拼接出redis的key
		if (headLineCondition.getEnableStatus() != null) {
			key = key + "_" + headLineCondition.getEnableStatus();
		}
		//判断key是否存在
		if (!jedisKeys.exists(key)) {
			//若不存在,则从数据库中取出相关数据,并在经过转换后放到redis缓存中
			headLineList = headLineDao.queryHeadLine(headLineCondition);
			String jsonString = mapper.writeValueAsString(headLineList);
			//jedisStrings.set(key, jsonString);
		} else {
			//如果在redis缓存中存在,则转换redis数据库中的数据后返回.
			String jsonString = jedisStrings.get(key);
			JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, HeadLine.class);
			headLineList = mapper.readValue(jsonString, javaType);
		}
		return headLineList;
	}

	@Override
	@Transactional
	public HeadLineExecution addHeadLine(HeadLine headLine, CommonsMultipartFile thumbnail) {
		if (headLine != null) {
			headLine.setCreateTime(new Date());
			headLine.setLastEditTime(new Date());
			if (thumbnail != null) {
				addThumbnail(headLine, thumbnail);
			}
			try {
				int effectedNum = headLineDao.insertHeadLine(headLine);
				if (effectedNum > 0) {
					String prefix = HLLISTKEY;
					Set<String> keySet = jedisKeys.keys(prefix + "*");
					for (String key : keySet) {
						jedisKeys.del(key);
					}
					return new HeadLineExecution(HeadLineStateEnum.SUCCESS, headLine);
				} else {
					return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("添加区域信息失败:" + e.toString());
			}
		} else {
			return new HeadLineExecution(HeadLineStateEnum.EMPTY);
		}
	}

	@Override
	@Transactional
	public HeadLineExecution modifyHeadLine(HeadLine headLine, CommonsMultipartFile thumbnail) {
		if (headLine.getLineId() != null && headLine.getLineId() > 0) {
			headLine.setLastEditTime(new Date());
			if (thumbnail != null) {
				HeadLine tempHeadLine = headLineDao.queryHeadLineById(headLine.getLineId());
				if (tempHeadLine.getLineImg() != null) {
					ImageUtils.deleteFileOrPath(tempHeadLine.getLineImg());
				}
				addThumbnail(headLine, thumbnail);
			}
			try {
				int effectedNum = headLineDao.updateHeadLine(headLine);
				if (effectedNum > 0) {
					String prefix = HLLISTKEY;
					Set<String> keySet = jedisKeys.keys(prefix + "*");
					for (String key : keySet) {
						jedisKeys.del(key);
					}
					return new HeadLineExecution(HeadLineStateEnum.SUCCESS, headLine);
				} else {
					return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("更新头条信息失败:" + e.toString());
			}
		} else {
			return new HeadLineExecution(HeadLineStateEnum.EMPTY);
		}
	}

	@Override
	@Transactional
	public HeadLineExecution removeHeadLine(long headLineId) {
		if (headLineId > 0) {
			try {
				HeadLine tempHeadLine = headLineDao.queryHeadLineById(headLineId);
				if (tempHeadLine.getLineImg() != null) {
					ImageUtils.deleteFileOrPath(tempHeadLine.getLineImg());
				}
				int effectedNum = headLineDao.deleteHeadLine(headLineId);
				if (effectedNum > 0) {
					String prefix = HLLISTKEY;
					Set<String> keySet = jedisKeys.keys(prefix + "*");
					for (String key : keySet) {
						jedisKeys.del(key);
					}
					return new HeadLineExecution(HeadLineStateEnum.SUCCESS);
				} else {
					return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("删除头条信息失败:" + e.toString());
			}
		} else {
			return new HeadLineExecution(HeadLineStateEnum.EMPTY);
		}
	}

	@Override
	@Transactional
	public HeadLineExecution removeHeadLineList(List<Long> headLineIdList) {
		if (headLineIdList != null && headLineIdList.size() > 0) {
			try {
				List<HeadLine> headLineList = headLineDao.queryHeadLineByIds(headLineIdList);
				for (HeadLine headLine : headLineList) {
					if (headLine.getLineImg() != null) {
						ImageUtils.deleteFileOrPath(headLine.getLineImg());
					}
				}
				int effectedNum = headLineDao.batchDeleteHeadLine(headLineIdList);
				if (effectedNum > 0) {
					String prefix = HLLISTKEY;
					Set<String> keySet = jedisKeys.keys(prefix + "*");
					for (String key : keySet) {
						jedisKeys.del(key);
					}
					return new HeadLineExecution(HeadLineStateEnum.SUCCESS);
				} else {
					return new HeadLineExecution(HeadLineStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("删除头条信息失败:" + e.toString());
			}
		} else {
			return new HeadLineExecution(HeadLineStateEnum.EMPTY);
		}
	}

	private void addThumbnail(HeadLine headLine, CommonsMultipartFile thumbnail) {
		//String dest =PathUtil.getHeadLineImagePath();
		//String thumbnailAddr = ImageUtils.generateNormalImg(thumbnail, dest);
		//headLine.setLineImg(thumbnailAddr);
	}

}
