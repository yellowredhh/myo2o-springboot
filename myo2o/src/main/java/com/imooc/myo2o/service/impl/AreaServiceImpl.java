package com.imooc.myo2o.service.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.imooc.myo2o.cache.JedisUtil;
import com.imooc.myo2o.dao.AreaDao;
import com.imooc.myo2o.dto.AreaExecution;
import com.imooc.myo2o.entity.Area;
import com.imooc.myo2o.enums.AreaStateEnum;
import com.imooc.myo2o.service.AreaService;

@Service
public class AreaServiceImpl implements AreaService {

	@Autowired
	private JedisUtil.Strings jedisStrings;
	@Autowired
	private JedisUtil.Keys jedisKeys;
	@Autowired
	private AreaDao areaDao;

	private static String AREALISTKEY = "arealist";

	/**
	 * 加入了redis缓存技术的区域信息访问,redis是key-value的数据库
	 */
	@Override
	public List<Area> getAreaList() throws JsonParseException, JsonMappingException, IOException {
		String key = AREALISTKEY;
		List<Area> areaList = null;
		//需要用到json字符串和对象之间的转换
		ObjectMapper mapper = new ObjectMapper();
		if (!jedisKeys.exists(key)) { //如果在redis数据库中没有缓冲区域信息,则先从持久化数据库中获取区域信息,然后进行类型转换,最后添加到redis数据中
			//从持久化数据库中获取区域信息
			areaList = areaDao.queryArea();
			//类型转换,从持久化数据库中获取到的区域信息是List类型的,redis作为String类型(也就是json)来存储.所以要进行转换
			String jsonString = mapper.writeValueAsString(areaList);
			//将转换结果存储到redis数据对应的key中作为缓存使用
			jedisStrings.set(key, jsonString);
		} else { //如果redis数据库中已经缓存了相关信息,则直接获取,然后进行转换
			String jsonString = jedisStrings.get(key);
			JavaType javaType = mapper.getTypeFactory().constructParametricType(ArrayList.class, Area.class);
			areaList = mapper.readValue(jsonString, javaType);
		}
		return areaList;
	}

	@Override
	@Transactional
	public AreaExecution addArea(Area area) {
		if (area.getAreaName() != null && !"".equals(area.getAreaName())) {
			area.setCreateTime(new Date());
			area.setLastEditTime(new Date());
			try {
				int effectedNum = areaDao.insertArea(area);
				if (effectedNum > 0) {
					String key = AREALISTKEY;
					if (jedisKeys.exists(key)) {
						jedisKeys.del(key);
					}
					return new AreaExecution(AreaStateEnum.SUCCESS, area);
				} else {
					return new AreaExecution(AreaStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("添加区域信息失败:" + e.toString());
			}
		} else {
			return new AreaExecution(AreaStateEnum.EMPTY);
		}
	}

	@Override
	@Transactional
	public AreaExecution modifyArea(Area area) {
		if (area.getAreaId() != null && area.getAreaId() > 0) {
			area.setLastEditTime(new Date());
			try {
				int effectedNum = areaDao.updateArea(area);
				if (effectedNum > 0) {
					String key = AREALISTKEY;
					if (jedisKeys.exists(key)) {
						jedisKeys.del(key);
					}
					return new AreaExecution(AreaStateEnum.SUCCESS, area);
				} else {
					return new AreaExecution(AreaStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("更新区域信息失败:" + e.toString());
			}
		} else {
			return new AreaExecution(AreaStateEnum.EMPTY);
		}
	}

	@Override
	@Transactional
	public AreaExecution removeArea(long areaId) {
		if (areaId > 0) {
			try {
				int effectedNum = areaDao.deleteArea(areaId);
				if (effectedNum > 0) {
					String key = AREALISTKEY;
					if (jedisKeys.exists(key)) {
						jedisKeys.del(key);
					}
					return new AreaExecution(AreaStateEnum.SUCCESS);
				} else {
					return new AreaExecution(AreaStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("删除区域信息失败:" + e.toString());
			}
		} else {
			return new AreaExecution(AreaStateEnum.EMPTY);
		}
	}

	@Override
	@Transactional
	public AreaExecution removeAreaList(List<Long> areaIdList) {
		if (areaIdList != null && areaIdList.size() > 0) {
			try {
				int effectedNum = areaDao.batchDeleteArea(areaIdList);
				if (effectedNum > 0) {
					String key = AREALISTKEY;
					if (jedisKeys.exists(key)) {
						jedisKeys.del(key);
					}
					return new AreaExecution(AreaStateEnum.SUCCESS);
				} else {
					return new AreaExecution(AreaStateEnum.INNER_ERROR);
				}
			} catch (Exception e) {
				throw new RuntimeException("删除区域信息失败:" + e.toString());
			}
		} else {
			return new AreaExecution(AreaStateEnum.EMPTY);
		}
	}
}
