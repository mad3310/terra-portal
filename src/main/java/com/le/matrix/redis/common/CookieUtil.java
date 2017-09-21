package com.le.matrix.redis.common;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by liuhao1 on 2015/12/7.
 */
public class CookieUtil {
    public static final String MATRIX_COOKIE_KEY = "matrix_cookie";
    public static final int MATRIX_COOKIE_AGE = 3600;//默认超时1800s即半小时
    public static final int MEMORY_MAX_AGE = -1; // 内存cookie

    public static final String COOKIE_KEY_USER_NAME = "userName";
    public static final String COOKIE_KEY_USER_ID = "userId";
    public static final String COOKIE_KEY_HEAD_PORTRAIT = "headPortrait";

    public static final String COOKIE_DOMAIN = "lecloud.com";
    private static final String DELETE_COOKIE_VALUE = null;
    private static final int DELETE_COOKIE_MAX_AGE = 0;

    public static void addCookieWithDomain(HttpServletResponse response,String name,String value,int maxAge, String domain){
        Cookie cookie = new Cookie(name,value);
        cookie.setPath("/");
        cookie.setMaxAge(maxAge);
        cookie.setDomain(domain);
        response.addCookie(cookie);
    }
    public static void delCookieByDomain(String loginCookieName, HttpServletResponse response, String domain) {
        addCookieWithDomain(response, loginCookieName, DELETE_COOKIE_VALUE, DELETE_COOKIE_MAX_AGE, domain);

    }
    /**Methods Name: addCookie <br>
     * Description: 添加cookie<br>
     * @author name: liuhao1
     * @param response
     * @param name
     * @param value
     * @param maxAge
     */
    public static void addCookie(HttpServletResponse response,String name,String value,int maxAge){
        Cookie cookie = new Cookie(name,value);
        cookie.setPath("/");
        if(maxAge>0)  cookie.setMaxAge(maxAge);
        response.addCookie(cookie);
    }

    /**Methods Name: getCookieByName <br>
     * Description: 根据cookie名称获取cookie<br>
     * @author name: liuhao1
     * @param request
     * @param name
     * @return
     */
    public static Cookie getCookieByName(HttpServletRequest request,String name){
        Map<String,Cookie> cookieMap = ReadCookieMap(request);
        if(!cookieMap.containsKey(name))
            return null;
        Cookie cookie = (Cookie)cookieMap.get(name);
        return cookie;
    }

    private static Map<String,Cookie> ReadCookieMap(HttpServletRequest request){
        Map<String,Cookie> cookieMap = new HashMap<String,Cookie>();
        Cookie[] cookies = request.getCookies();
        if(null!=cookies){
            for(Cookie cookie : cookies){
                cookieMap.put(cookie.getName(), cookie);
            }
        }
        return cookieMap;
    }

}
