package cn.edu.dbsi.controller;

import cn.edu.dbsi.interceptor.LoginRequired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by 郭世明 on 2017/7/17.
 */
@LoginRequired
@RestController
@RequestMapping(value = "/rest")
public class OlapStoryboardElementInfoController {
}
