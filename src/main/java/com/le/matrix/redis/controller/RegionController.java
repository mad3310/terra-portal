package com.le.matrix.redis.controller;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.le.matrix.redis.facade.IRedisService;
import com.le.matrix.redis.rest.enumeration.RestAPIFormatter;
import com.le.matrix.redis.rest.exception.ApiException;
import com.letv.common.result.ApiResultObject;
import com.letv.common.session.SessionServiceImpl;


/**
 * redis地域操作
 * @author lisuxiao
 *
 */
@Controller
@RequestMapping("/redis/region")
public class RegionController {
    private final static Logger logger = LoggerFactory.getLogger(RegionController.class);

    @Autowired
    private IRedisService redisService;
    @Autowired(required=false)
    private SessionServiceImpl sessionService;

    
    /**
     * 获取redis 地域信息列表
     * @param page
     * @param request
     * @return
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<Map> query() {
    	logger.info("开始查询redis地域信息列表");
    	
    	ApiResultObject ret = redisService.getReidsRegion();
    	if(!ret.getAnalyzeResult()) {
    		throw new ApiException(RestAPIFormatter.ServerError.formatErrorMessage(ret.getResult()));
    	}
    	List<Map> result = JSONObject.parseArray(ret.getResult(), Map.class);
    	logger.info("查询redis地域信息列表成功,{}", ret.getResult());
    	return result;
    }
    
    /**
     * 获取region下的可用区
     * @param regionId
     * @return
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{regionId}/az", method = RequestMethod.GET)
    public @ResponseBody List<Map> getAz(@PathVariable Long regionId) {
    	logger.info("开始查询redis地域下可用区列表");
    	
    	ApiResultObject ret = redisService.getReidsRegionAz(regionId);
    	if(!ret.getAnalyzeResult()) {
    		throw new ApiException(RestAPIFormatter.ServerError.formatErrorMessage(ret.getResult()));
    	}
    	List<Map> result = JSONObject.parseArray(ret.getResult(), Map.class);
    	logger.info("查询redis地域可用区列表成功,{}", ret.getResult());
    	return result;
    }
    

}
