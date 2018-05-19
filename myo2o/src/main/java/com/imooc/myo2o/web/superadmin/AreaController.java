package com.imooc.myo2o.web.superadmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.imooc.myo2o.entity.Area;
import com.imooc.myo2o.service.AreaService;

@Controller
@RequestMapping("/areacontroller")
public class AreaController {

	Logger logger = LoggerFactory.getLogger(AreaController.class);

	@Autowired
	private AreaService areaService;

	@RequestMapping(value = "/getarealist", method = RequestMethod.GET)
	@ResponseBody
	private Map<String, Object> getAreaList() {
		logger.info("--start--");
		long startTime = System.currentTimeMillis();
		Map<String, Object> areaMap = new HashMap<String, Object>();
		List<Area> areaList = new ArrayList<>();
		try {
			areaList = areaService.getAreaList();
			areaMap.put("rows", areaList);
			areaMap.put("total", areaList.size());
		} catch (Exception e) {
			e.printStackTrace();
			areaMap.put("success", false);
			areaMap.put("errMsg", e.toString());
			logger.error("test error");
		}
		logger.error("==error==");
		long endTime = System.currentTimeMillis();
		logger.debug("costTime:[{}ms]",endTime-startTime);
		logger.info("--end--");
		return areaMap;
	}
}
