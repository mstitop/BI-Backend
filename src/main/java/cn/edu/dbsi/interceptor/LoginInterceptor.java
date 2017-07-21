package cn.edu.dbsi.interceptor;

import cn.edu.dbsi.security.JwtTokenUtil;
import org.datanucleus.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 郭世明 on 2017/7/21.
 * 自定义拦截器，继承自HandlerInterceptor，用于处理用户权限认证
 */
public class LoginInterceptor implements HandlerInterceptor {

    private String tokenHeader = "Authorization";

    private String tokenHead = "Bearer ";

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String authHeader = httpServletRequest.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            final String authToken = authHeader.substring(tokenHead.length());

            if (JwtTokenUtil.validateToken(authToken, (String) httpServletRequest.getSession().getAttribute("id"))) {
                return true;
            }
        }
        httpServletResponse.setStatus(401);
        return false;
    }

    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {

    }

    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {

    }
}
