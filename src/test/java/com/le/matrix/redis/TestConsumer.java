package com.le.matrix.redis;

import java.io.IOException;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.le.matrix.redis.facade.IDemoService;
import com.le.matrix.redis.model.Demo;

/**
 * Created by linzhanbo on 2016/10/10.
 */
public class TestConsumer {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext(
                new String[]{"spring/spring-context.xml"});
        context.start();
        IDemoService facade = (IDemoService) context.getBean("demoFacade");
        Demo result = facade.selectByPrimaryKey(1);
        System.out.println("result:" + result);
        System.out.println("Press any key to exit.");
        try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
