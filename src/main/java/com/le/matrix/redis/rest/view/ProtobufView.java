package com.le.matrix.redis.rest.view;

import org.springframework.web.servlet.view.AbstractView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

public class ProtobufView extends AbstractView {
    public ProtobufView() {
        setContentType("application/x-protobuf");
    }

    @Override
    protected void renderMergedOutputModel(Map<String, Object> model,
                                           HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        System.out.println("11111");
    }

}