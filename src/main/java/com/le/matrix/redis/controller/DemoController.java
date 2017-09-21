package com.le.matrix.redis.controller;

import com.le.matrix.redis.facade.IDemoService;
import com.le.matrix.redis.model.Demo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by linzhanbo on 2016/10/11.
 */
@Controller
@RequestMapping("/demo")
public class DemoController {
    private final static Logger logger = LoggerFactory.getLogger(DemoController.class);

    @Autowired
    private IDemoService demo;

    /**
     * 使用AnnotationMethodHandlerAdapter
     *
     * @param userId
     * @return
     */
    @RequestMapping(value = "/{userId}", method = RequestMethod.GET)
    public
    @ResponseBody
    Demo getUser(@PathVariable Integer userId) {
        Demo user = demo.selectByPrimaryKey(userId);
        return user;
    }

    /**
     * 使用ContentNegotiatingViewResolver
     *
     * @param userId
     * @param model
     * @return
     */
    @RequestMapping(value = "/api/{userId}", method = RequestMethod.GET)
    public void getUser(@PathVariable Integer userId, ModelMap model) {
        Demo user = demo.selectByPrimaryKey(userId);
        model.addAttribute("model", user);
    }

    /**
     * 使用ContentNegotiatingViewResolver
     *
     * @param userId
     * @param mav
     * @return
     */
    @RequestMapping(value = "/jsp/{userId}", method = RequestMethod.GET)
    public ModelAndView getUser2(@PathVariable Integer userId, ModelAndView mav) {
        Demo user = demo.selectByPrimaryKey(userId);
        mav.addObject("user", user);
        mav.setViewName("list");
        return mav;
    }

    /**
     * 使用AnnotationMethodHandlerAdapter
     *
     * @param userId
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    Demo save(@Valid Demo user, BindingResult bindResult) {
        if (bindResult.hasErrors()) {
            logger.error("校验参数不合法");
            List<ObjectError> errors = bindResult.getAllErrors();
            List<String> msgs = new ArrayList<>();
            for (ObjectError error : errors) {
                msgs.add(((DefaultMessageSourceResolvable) error.getArguments()[0]).getDefaultMessage() + ":" + error.getDefaultMessage());
            }
        }
        demo.insert(user);
        return user;
    }

    /**
     * 使用ContentNegotiatingViewResolver
     *
     * @param userId
     * @param mav
     * @return
     */
    @RequestMapping(value = "/api", method = RequestMethod.POST)
    public void save(@Valid Demo user, BindingResult bindResult, ModelMap model) {
        if (bindResult.hasErrors()) {
            logger.error("校验参数不合法");
            List<ObjectError> errors = bindResult.getAllErrors();
            List<String> msgs = new ArrayList<>();
            for (ObjectError error : errors) {
                msgs.add(((DefaultMessageSourceResolvable) error.getArguments()[0]).getDefaultMessage() + ":" + error.getDefaultMessage());
            }
        }
        demo.insert(user);
        model.addAttribute("model", user);
    }
}
