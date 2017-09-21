package com.le.matrix.redis.interceptor;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.le.matrix.redis.common.CookieUtil;
import com.le.matrix.redis.facade.ILoginService;
import com.letv.common.result.ResultObject;
import com.letv.common.session.Executable;
import com.letv.common.session.Session;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.SessionUtil;

/**
 * Created by linzhanbo on 2016/10/11.
 */
@Component
public class SessionTimeoutInterceptor implements HandlerInterceptor {
	
	@Autowired
    private ILoginService loginService;
	@Autowired(required=false)
    private SessionServiceImpl sessionService;
	private static String OAUTH_CLIENT_ID = "client_id";
    private static String OAUTH_CLIENT_SECRET = "client_secret";
	
    public String[] allowUrls;

    public void setAllowUrls(String[] allowUrls) {
        this.allowUrls = allowUrls;
    }
    
    private boolean allowUrl(HttpServletRequest request){
        String requestUrl = request.getRequestURI().replace(request.getContextPath(), "");
        if("/".equals(requestUrl)) {
            return true;
        }
        //特殊url过滤
        if(null != allowUrls && allowUrls.length>=1) {
            for(String url : allowUrls) {
                if(requestUrl.contains(url)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object arg2) throws Exception {
        if(allowUrl(request))
            return true;

        Cookie matrixCookie = CookieUtil.getCookieByName(request, CookieUtil.MATRIX_COOKIE_KEY);
        Session session = null;
        if (null != matrixCookie) {
            session = this.loginService.getUserByToken(matrixCookie.getValue());
        }
        if(null == session) {
            //session is null,login by request
            String clientId = request.getParameter(OAUTH_CLIENT_ID);
            String clientSecret = request.getParameter(OAUTH_CLIENT_SECRET);
            
            if(null != clientId && null != clientSecret) {
            	session = this.loginService.login(clientId,clientSecret);
            }
        }

        //login success,session not null.
        if(null != session)
            return loginSuccess(session, request,response);

        //login failed by request.
        return toLogin(request,response);

    }

    private boolean toLogin(HttpServletRequest request,HttpServletResponse response) throws Exception{
        CookieUtil.delCookieByDomain(CookieUtil.MATRIX_COOKIE_KEY,response,CookieUtil.COOKIE_DOMAIN);
        CookieUtil.delCookieByDomain(CookieUtil.COOKIE_KEY_USER_ID,response,CookieUtil.COOKIE_DOMAIN);
        CookieUtil.delCookieByDomain(CookieUtil.COOKIE_KEY_USER_NAME,response,CookieUtil.COOKIE_DOMAIN);
        boolean isAjaxRequest = (request.getHeader("x-requested-with") != null)? true:false;
        if (isAjaxRequest) {
            responseJson(request,response,"长时间未操作，请重新登录");
        } else {
            RequestDispatcher rd = request.getRequestDispatcher("/toLogin?back="+request.getRequestURI());
            rd.forward(request, response);
        }
        return false;
    }
    
    private boolean loginSuccess(Session session,HttpServletRequest request,HttpServletResponse response){
        String cookieStr = SessionUtil.generateSessionId(session.getOauthId(), session.getClientId(), session.getClientSecret());
        CookieUtil.addCookieWithDomain(response,CookieUtil.MATRIX_COOKIE_KEY,cookieStr,CookieUtil.MATRIX_COOKIE_AGE,CookieUtil.COOKIE_DOMAIN);
        CookieUtil.addCookieWithDomain(response, CookieUtil.COOKIE_KEY_USER_ID, String.valueOf(session.getUserId()), CookieUtil.MEMORY_MAX_AGE, CookieUtil.COOKIE_DOMAIN);
        CookieUtil.addCookieWithDomain(response, CookieUtil.COOKIE_KEY_USER_NAME, String.valueOf(session.getUserName()), CookieUtil.MEMORY_MAX_AGE, CookieUtil.COOKIE_DOMAIN);
        sessionService.runWithSession(session, "Usersession changed", new Executable<Session>(){
            @Override
            public Session execute() throws Throwable {
                return null;
            }
        });
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {

    }
    
    private void responseJson(HttpServletRequest req, HttpServletResponse res, String message) {
        PrintWriter out = null;
        try {
            res.setContentType("text/html;charset=UTF-8");
            out = res.getWriter();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
        ResultObject resultObject = new ResultObject(0);
        resultObject.addMsg(message);
        out.append(JSON.toJSONString(resultObject, SerializerFeature.WriteMapNullValue));
        out.flush();
    }
    
}
