package com.le.matrix.redis.controller;

import java.beans.PropertyEditor;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.PropertyEditorRegistry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import com.le.matrix.redis.enumeration.RedisType;
import com.le.matrix.redis.junitBase.AbstractTest;
import com.le.matrix.redis.model.Redis;
import com.letv.common.paging.impl.Page;

public class RedisControllerTest extends AbstractTest{

	@Autowired
	private RedisController redisController;
	
	private final static Logger logger = LoggerFactory.getLogger(
			RedisControllerTest.class);
	
	private int totalSize = 0;
	private String redisName = null;
	
	@Before
    public void testControllerBefore() {
    	request.setAttribute("currentPage", 1);
    	request.setAttribute("recordsPerPage", 15);
    	Page p = redisController.query(new Page(), request);
    	totalSize = p.getTotalRecords();
    	logger.info("查询获取totalSize：{}", totalSize);
    }
    
    @Test
    public void testInsertOneEs() throws InterruptedException {
    	BindingResult br = new BindingResult() {
			@Override
			public void setNestedPath(String nestedPath) {
			}
			@Override
			public void rejectValue(String field, String errorCode, Object[] errorArgs,
					String defaultMessage) {
			}
			@Override
			public void rejectValue(String field, String errorCode,
					String defaultMessage) {
			}
			@Override
			public void rejectValue(String field, String errorCode) {
			}
			@Override
			public void reject(String errorCode, Object[] errorArgs,
					String defaultMessage) {
			}
			@Override
			public void reject(String errorCode, String defaultMessage) {
			}
			@Override
			public void reject(String errorCode) {
			}
			@Override
			public void pushNestedPath(String subPath) {
			}
			@Override
			public void popNestedPath() throws IllegalStateException {
			}
			@Override
			public boolean hasGlobalErrors() {
				return false;
			}
			@Override
			public boolean hasFieldErrors(String field) {
				return false;
			}
			@Override
			public boolean hasFieldErrors() {
				return false;
			}
			@Override
			public boolean hasErrors() {
				return false;
			}
			@Override
			public String getObjectName() {
				return null;
			}
			@Override
			public String getNestedPath() {
				return null;
			}
			@Override
			public List<ObjectError> getGlobalErrors() {
				return null;
			}
			@Override
			public int getGlobalErrorCount() {
				return 0;
			}
			@Override
			public ObjectError getGlobalError() {
				return null;
			}
			@Override
			public Object getFieldValue(String field) {
				return null;
			}
			@Override
			public Class<?> getFieldType(String field) {
				return null;
			}
			@Override
			public List<FieldError> getFieldErrors(String field) {
				return null;
			}
			@Override
			public List<FieldError> getFieldErrors() {
				return null;
			}
			@Override
			public int getFieldErrorCount(String field) {
				return 0;
			}
			@Override
			public int getFieldErrorCount() {
				return 0;
			}
			@Override
			public FieldError getFieldError(String field) {
				return null;
			}
			@Override
			public FieldError getFieldError() {
				return null;
			}
			@Override
			public int getErrorCount() {
				return 0;
			}
			@Override
			public List<ObjectError> getAllErrors() {
				return null;
			}
			@Override
			public void addAllErrors(Errors errors) {
			}
			@Override
			public String[] resolveMessageCodes(String errorCode, String field) {
				return null;
			}
			@Override
			public void recordSuppressedField(String field) {
			}
			@Override
			public Object getTarget() {
				return null;
			}
			@Override
			public String[] getSuppressedFields() {
				return null;
			}
			@Override
			public Object getRawFieldValue(String field) {
				return null;
			}
			@Override
			public PropertyEditorRegistry getPropertyEditorRegistry() {
				return null;
			}
			@Override
			public Map<String, Object> getModel() {
				return null;
			}
			@Override
			public PropertyEditor findEditor(String field, Class<?> valueType) {
				return null;
			}
			@Override
			public void addError(ObjectError error) {
			}
		};
		Redis r = new Redis();
    	r.setName("junitTest"+System.currentTimeMillis());
    	r.setMemorySize(5);
    	r.setType(RedisType.RedisCluster);
    	r.setConfigId("test_config_id");
    	r.setDescn("test_descn");
    	r.setCreateUser(userId);
    	redisController.insert(r, br);
    	redisName = r.getName();
    	logger.info("redis创建成功，redisName：{}", redisName);
    	
    	Page p = redisController.query(new Page(), request);
    	Assert.assertEquals(totalSize+1, p.getTotalRecords());
    }
    
    @After
    public void testControllerAfter() {
    	request.setAttribute("name", redisName);
    	Page p = redisController.query(new Page(), request);
    	Long redisId = ((List<Redis>)p.getData()).get(0).getId();
    	redisController.delete(redisId);
    	logger.info("redis删除成功，redisId：{}", redisId);
    	
    	request.removeParameter("name");
    	p = redisController.query(new Page(), request);
    	Assert.assertEquals(totalSize, p.getTotalRecords());
    }

}
