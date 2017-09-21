package com.le.matrix.redis.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.http.auth.AuthenticationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.le.matrix.redis.common.Constant;
import com.le.matrix.redis.common.CookieUtil;
import com.le.matrix.redis.facade.ILoginService;
import com.le.matrix.redis.rest.enumeration.RestAPIFormatter;
import com.le.matrix.redis.rest.exception.ApiException;
import com.letv.common.session.Executable;
import com.letv.common.session.Session;
import com.letv.common.session.SessionServiceImpl;
import com.letv.common.util.HttpsClient;
import com.letv.common.util.SessionUtil;
import com.letv.mms.cache.ICacheService;
import com.letv.mms.cache.factory.CacheFactory;

/**
 * Created by linzhanbo on 2016/10/11.
 */
@Controller
@RequestMapping("/login")
public class LoginController {
    private final static Logger logger = LoggerFactory.getLogger(LoginController.class);

    @Autowired
    private ILoginService loginService;
    @Autowired(required=false)
    private SessionServiceImpl sessionService;
    private ICacheService<?> cacheService = CacheFactory.getCache();
    @Value("${oauth.auth.http}")
    private String OAUTH_AUTH_HTTP;
    @Value("${terra.portal.http}")
    private String TERRA_PORTAL_HTTP;

    private final static String OAUTH_REDIRECT_KEY_SECRET = "&app_key=chenle&app_secret=newpasswd";

    /**
     * 使用AnnotationMethodHandlerAdapter
     *
     * @param userId
     * @return
     */
    @RequestMapping(method = RequestMethod.POST)
    public
    @ResponseBody
    Map<String, Object> login(HttpServletRequest request) {
    	String clientId = request.getHeader(Constant.CLIENT_ID);
        String clientSecret = request.getHeader(Constant.CLIENT_SECRET);
        if(StringUtils.isEmpty(clientId)) {
			throw new ApiException(RestAPIFormatter.MissingClientId);
        }
		if(StringUtils.isEmpty(clientSecret)) {
			throw new ApiException(RestAPIFormatter.MissingClientSecret);
		}
		Session session = null;
        try {
        	session = loginService.login(clientId, clientSecret);
		} catch (AuthenticationException e) {
			throw new ApiException(RestAPIFormatter.Unauthorized);
		} catch (IllegalArgumentException e) {
			throw e;
		}
        Map<String, Object> ret = new HashMap<String, Object>();
        if(null != session) {
        	String accessToken = SessionUtil.generateSessionId(session.getOauthId(), session.getClientId(), session.getClientSecret());
        	ret.put(Constant.ACCESS_TOKEN, accessToken);
        }
        return ret;
    }
    
    @RequestMapping(value = "/out", method = RequestMethod.GET)
    public @ResponseBody void logout(HttpServletRequest request, HttpServletResponse response) throws IOException {
		logger.debug("logout start");

		sessionService.runWithSession(null, "用户退出", new Executable<Session>() {
			@Override
			public Session execute() throws Throwable {
				return null;
			}
		});
		
		Session session = (Session) request.getSession().getAttribute(Session.USER_SESSION_REQUEST_ATTRIBUTE);
        Cookie cookie = CookieUtil.getCookieByName(request, CookieUtil.MATRIX_COOKIE_KEY);
        if(null != cookie) {
            session = (Session) this.cacheService.get(SessionUtil.getUuidBySessionId(cookie.getValue()),null);
            CookieUtil.delCookieByDomain(CookieUtil.MATRIX_COOKIE_KEY,response,CookieUtil.COOKIE_DOMAIN);
            CookieUtil.delCookieByDomain(CookieUtil.COOKIE_KEY_USER_ID,response,CookieUtil.COOKIE_DOMAIN);
            CookieUtil.delCookieByDomain(CookieUtil.COOKIE_KEY_USER_NAME,response,CookieUtil.COOKIE_DOMAIN);
        }
        if(session != null) {
            String clientId = session.getClientId();
            String clientSecret = session.getClientSecret();

            StringBuffer buffer = new StringBuffer();
            buffer.append(OAUTH_AUTH_HTTP).append("/logout?client_id=").append(clientId).append("&client_secret=").append(clientSecret).append(OAUTH_REDIRECT_KEY_SECRET);

            sessionService.setSession(null,"logout");
            String result = HttpsClient.sendXMLDataByGet(buffer.toString(), 1000, 1000);
            logger.info("loginout by oauth,url:{},result:{}",buffer.toString(),result);
        }
        logger.debug("logouted successfully");
        StringBuffer buffer = new StringBuffer();
        buffer.append(OAUTH_AUTH_HTTP).append("/index?redirect_uri=").append(TERRA_PORTAL_HTTP).append("/oauth/callback");
        response.sendRedirect(buffer.toString());
	}

}
