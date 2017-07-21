package cn.edu.dbsi.interceptor;

import java.lang.annotation.*;

/**
 * Created by 郭世明 on 2017/7/21.
 *
 * 自定义注解，被加上该注解的URL都会通过自定义的拦截器进行过滤
 */

@Target({ElementType.TYPE,ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
@Documented
public @interface LoginRequired {
}
