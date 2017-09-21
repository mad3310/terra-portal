package com.le.matrix.redis.controller;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.le.matrix.redis.common.GenerateUtil;
import com.le.matrix.redis.enumeration.Status;
import com.le.matrix.redis.facade.IRedisService;
import com.le.matrix.redis.model.Redis;
import com.le.matrix.redis.rest.enumeration.RestAPIFormatter;
import com.le.matrix.redis.rest.exception.ApiException;
import com.letv.common.exception.ValidateException;
import com.letv.common.paging.impl.Page;
import com.letv.common.result.ApiResultObject;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.HttpUtil;


/**
 * redis操作
 * @author lisuxiao
 *
 */
@Controller
@RequestMapping("/redis")
public class RedisController {
    private final static Logger logger = LoggerFactory.getLogger(RedisController.class);

    @Autowired
    private IRedisService redisService;
    @Autowired(required=false)
    private SessionServiceImpl sessionService;

    
    /**
     * 获取带分页redis信息列表
     * @param page
     * @param request
     * @return
     */
    @RequestMapping(method = RequestMethod.GET)
    public @ResponseBody Page query(Page page, HttpServletRequest request) {
    	logger.info("开始查询redis信息列表");
    	
    	Map<String,Object> params = HttpUtil.requestParam2Map(request);
    	params.put("createUser", this.sessionService.getSession().getUserId());
    	
    	Page ret = redisService.queryByPagination(page, params);
    	
    	logger.info("查询redis信息列表成功，总条数{}", ret.getTotalRecords());
    	return ret;
    }
    
    @RequestMapping(method = RequestMethod.POST)
    public @ResponseBody void insert(@Valid Redis redis, BindingResult bindResult) {
    	logger.info("开始保存redis信息");
    	
    	if (bindResult.hasErrors()) {
			logger.warn("校验参数不合法");
			throw new ApiException(RestAPIFormatter.ParamsInvalid.formatErrorMessage(bindResult.getAllErrors()));
		}
    	
    	ApiResultObject nameExist = this.redisService.checkNameExist(redis.getName());
    	ApiResultObject canCreate = this.redisService.checkCanCreate(Long.parseLong(redis.getAzId()), redis.getMemorySize());
    	
    	if(!nameExist.getAnalyzeResult()) {
    		throw new ApiException(RestAPIFormatter.ServiceIsExist.formatErrorMessage(redis.getName()));
    	}
    	if(!canCreate.getAnalyzeResult()) {
    		throw new ApiException(RestAPIFormatter.ServiceIsNonUse.formatErrorMessage("该可用区资源不足，"));
    	}
    	
    	redis.setCreateTime(new Timestamp(System.currentTimeMillis()));
    	redis.setCreateUser(this.sessionService.getSession().getUserId());
    	redis.setPassword(GenerateUtil.generatePassword());
    	redis.setStatus(Status.PENDING);
    	
    	redisService.insert(redis);
    	
    	logger.info("保存redis信息成功");
    }
    
    @RequestMapping(value = "/{id}/db", method = RequestMethod.DELETE)
    public @ResponseBody void delete(@PathVariable Long id) {
    	logger.info("开始删除redis db信息，id:{}", id);
    	
    	validateAuthById(id);
    	Redis r = new Redis();
    	r.setId(id);
    	redisService.delete(r);
    	
    	logger.info("删除redis db信息成功，id:{}", id);
    }
    
    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public @ResponseBody void deleteDbAndInstance(@PathVariable Long id) {
    	logger.info("开始删除redis db和实例信息，id:{}", id);
    	
    	validateAuthById(id);
    	ApiResultObject apiResult = this.redisService.deleteDbAndInstance(id);
    	analyzeResult(apiResult);
    	
    	logger.info("删除redis db和实例成功，id:{}", id);
    }
    
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/checkNameExist", method = RequestMethod.POST)
    public @ResponseBody Map checkNameExist(@RequestParam String name) {
    	logger.info("开始查询redis名称是否存在，name:{}", name);
    	
    	ApiResultObject ret = redisService.checkNameExist(name);
    	
    	Map result = JSONObject.parseObject(ret.getResult(), Map.class);
    	
    	logger.info("查询redis名称是否存在成功，result:{}", result);
    	return result;
    }
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/checkCanCreate", method = RequestMethod.POST)
    public @ResponseBody Map checkCanCreate(@RequestParam Long azId, @RequestParam Integer memorySize) {
    	logger.info("开始查询内存和可用区是否可以创建，azId:{}, memorySize:{}", azId, memorySize);
    	
    	ApiResultObject ret = redisService.checkCanCreate(azId, memorySize);
    	
    	Map result = JSONObject.parseObject(ret.getResult(), Map.class);
    	
    	logger.info("查询内存和可用区是否可以创建，result:{}", result);
    	return result;
    }
    
    @SuppressWarnings("rawtypes")
	@RequestMapping(value = "/{id}/status", method = RequestMethod.GET)
    public @ResponseBody Map getStatusById(@PathVariable Long id) {
    	logger.info("开始查询redis状态，id:{}", id);
    	
    	List<Redis> rediss = validateAuthById(id);
    	ApiResultObject ret = redisService.getStatusByServiceId(Long.parseLong(rediss.get(0).getServiceId()));
    	analyzeResult(ret);
    	
    	Map result = JSONObject.parseObject(ret.getResult(), Map.class);
    	
    	logger.info("查询redis状态成功，result:{}", result);
    	return result;
    }
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public @ResponseBody Map getRedisById(@PathVariable Long id) {
    	logger.info("开始查询redis信息，id:{}", id);
    	
    	validateAuthById(id);
    	ApiResultObject ret = redisService.getRedisById(id);
    	analyzeResult(ret);
    	
    	Map result = JSONObject.parseObject(ret.getResult(), Map.class);
    	
    	logger.info("查询redis信息成功，result:{}", result);
    	return result;
    }
    
    @RequestMapping(value = "/{id}/password", method = RequestMethod.GET)
    public @ResponseBody Map<String, String> getRedisPasswordById(@PathVariable Long id) {
    	logger.info("开始查询redis password信息，id:{}", id);
    	
    	List<Redis> rediss = validateAuthById(id);
    	Map<String, String> ret = new HashMap<String, String>();
    	ret.put("password", rediss.get(0).getPassword());
    	
    	logger.info("查询redis passwrod信息成功，result:{}", ret);
    	return ret;
    }

    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/{id}/offline", method = RequestMethod.GET)
    public @ResponseBody Map offline(@PathVariable Long id) {
    	logger.info("开始下线redis实例，id:{}", id);
    	
    	List<Redis> rediss = validateAuthById(id);
    	ApiResultObject ret = redisService.offline(Long.parseLong(rediss.get(0).getServiceId()));
    	analyzeResult(ret);
    	
    	Map result = JSONObject.parseObject(ret.getResult(), Map.class);
    	
    	logger.info("下线redis实例成功，result:{}", result);
    	return result;
    }
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/{id}/start", method = RequestMethod.GET)
    public @ResponseBody Map start(@PathVariable Long id) {
    	logger.info("开始启动redis实例，id:{}", id);
    	
    	List<Redis> rediss = validateAuthById(id);
    	ApiResultObject ret = redisService.start(Long.parseLong(rediss.get(0).getServiceId()));
    	analyzeResult(ret);
    	
    	Map result = JSONObject.parseObject(ret.getResult(), Map.class);
    	
    	logger.info("启动redis实例成功，result:{}", result);
    	return result;
    }
    
    @SuppressWarnings("rawtypes")
    @RequestMapping(value = "/{id}/instance", method = RequestMethod.DELETE)
    public @ResponseBody Map deleteInstance(@PathVariable Long id) {
    	logger.info("开始删除redis实例，id:{}", id);
    	
    	List<Redis> rediss = validateAuthById(id);
    	ApiResultObject ret = redisService.deleteInstance(Long.parseLong(rediss.get(0).getServiceId()));
    	analyzeResult(ret);
    	
    	Map result = JSONObject.parseObject(ret.getResult(), Map.class);
    	
    	logger.info("删除redis实例成功，result:{}", result);
    	return result;
    }
    
    private List<Redis> validateAuthById(Long id) {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("id", id);
    	params.put("createUser", this.sessionService.getSession().getUserId());
    	List<Redis> rediss = this.redisService.selectByMap(params);
    	if(CollectionUtils.isEmpty(rediss)) {
    		throw new ValidateException("非法用户！");
    	}
    	return rediss;
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
