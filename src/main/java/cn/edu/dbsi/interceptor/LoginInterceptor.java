package cn.edu.dbsi.interceptor;

import cn.edu.dbsi.security.JwtToken;
import cn.edu.dbsi.security.JwtTokenManager;
import cn.edu.dbsi.service.LoginServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by 郭世明 on 2017/7/21.
 * 自定义拦截器，继承自HandlerInterceptor，用于处理用户权限认证
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    private String tokenHeader = "Authorization";

    private String tokenHead = "Bearer ";

    //在此类中使@Autowired注解时，那么在其他使用该类的对象中，也必须使用该注解
    @Autowired
    private JwtToken jwtTokenUtil;

    @Autowired
    private LoginServiceI loginServiceI;

    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o) throws Exception {
        String authHeader = httpServletRequest.getHeader(this.tokenHeader);
        if (authHeader != null && authHeader.startsWith(tokenHead)) {
            final String authToken = authHeader.substring(tokenHead.length());
            final String username = jwtTokenUtil.getUsernameFromToken(authToken);
            if (loginServiceI.getUserByUserId(Integer.parseInt(username)) != null) {
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
