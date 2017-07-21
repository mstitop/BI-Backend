package cn.edu.dbsi.controller;

import cn.edu.dbsi.model.User;
import cn.edu.dbsi.security.JwtTokenUtil;
import cn.edu.dbsi.service.LoginServiceI;
import cn.edu.dbsi.util.StatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/6/2.
 */
@RestController
@RequestMapping(value = "/login")
public class LoginController {

    @Autowired
    private LoginServiceI loginServiceI;

    @RequestMapping(method = RequestMethod.GET)
    public ResponseEntity<?> checkUser(@RequestParam String account, @RequestParam String password,HttpServletRequest request) {
        User user = loginServiceI.getUserByUsernameAndPassword(account, password);
        if (user != null) {
            if (user.getIsExist() == 1) {
                Map<String, Object> result = new HashMap<String, Object>();
                Map<String, Object> map = new HashMap<String, Object>();
                Map<String, Object> tokenMap = new HashMap<String, Object>();
                request.getSession().setAttribute("id",user.getUserid().toString());
                result.put("success",true);
                //数据库中为null时，则返回""
                map.put("icon ", user.getIcon() == null ? "" : user.getIcon());
                map.put("name", user.getRealname() == null ? "" : user.getRealname());
                map.put("sex", user.getSex() == null ? "" : user.getSex());
                map.put("position", user.getPosition() == null ? "" : user.getPosition());
                map.put("power", user.getPower() == null ? "" : user.getPower());
                map.put("phoneNumber", user.getPhonenumber() == null ? "" : user.getPhonenumber());
                map.put("address", user.getAddress() == null ? "" : user.getAddress());
                map.put("description", user.getDescription() == null ? "" : user.getDescription());
                result.put("person",map);
                String token = JwtTokenUtil.generateToken(user);
                tokenMap.put("token","Bearer "+token);
                result.put("auth",tokenMap);
                return StatusUtil.querySuccess(result);
            } else {
                return StatusUtil.error("","该用户不存在！");
            }
        } else {
            return StatusUtil.error("","该用户不存在！");
        }
    }
}
