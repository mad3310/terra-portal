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
@RequestMapping("/redis/config")
public class ConfigController {
    private final static Logger logger = LoggerFactory.getLogger(ConfigController.class);

    @Autowired
    private IRedisService redisService;
    @Autowired(required=false)
    private SessionServiceImpl sessionService;

    
    /**
     * 获取redis 配置信息列表
     * @return
     */
    @SuppressWarnings("rawtypes")
	@RequestMapping(method = RequestMethod.GET)
    public @ResponseBody List<Map> query() {
    	logger.info("开始查询redis配置信息列表");
    	
    	ApiResultObject ret = redisService.getReidsConfig();
    	analyzeResult(ret);
    	
    	List<Map> result = JSONObject.parseArray(ret.getResult(), Map.class);
    	logger.info("查询redis配置信息列表成功,{}", ret.getResult());
    	return result;
    }
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/{configId}", method = RequestMethod.GET)
    public @ResponseBody Map getConfigInfoByConfigId(@PathVariable Long configId) {
    	logger.info("开始根据id查询redis配置信息，configId:{}", configId);
    	
    	ApiResultObject ret = redisService.getReidsConfigByConfigId(configId);
    	analyzeResult(ret);
    	
    	Map result = JSONObject.parseObject(ret.getResult(), Map.class);
    	
    	logger.info("根据id查询redis配置信息成功，result:{}", result);
    	return result;
    }
    
    /**
     * 返回值逻辑校验
     * @param ret
     */
    private void analyzeResult(ApiResultObject ret) {
    	if(!ret.getAnalyzeResult()) {
    		throw new ApiException(RestAPIFormatter.ServerError.formatErrorMessage(ret.getResult()));
    	}
    }

}
