package cn.edu.dbsi.controller;

import cn.edu.dbsi.model.User;
import cn.edu.dbsi.security.JwtToken;
import cn.edu.dbsi.service.LoginServiceI;
import cn.edu.dbsi.util.StatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/6/2.
 */
@RestController
@RequestMapping
public class LoginController {

    @Autowired
    private LoginServiceI loginServiceI;

    @Autowired
    private JwtToken jwtTokenUtil;


    /**
     * 用户登录
     * @param account
     * @param password
     * @return
     */
    @RequestMapping(value = "/token",method = RequestMethod.GET)
    public ResponseEntity<?> checkUser(@RequestParam String account, @RequestParam String password) {
        User user = loginServiceI.getUserByUsernameAndPassword(account, password);
        if (user != null) {
            if (user.getIsExist() == 1) {
                Map<String, Object> result = new HashMap<String, Object>();
                Map<String, Object> map = new HashMap<String, Object>();
                Map<String, Object> tokenMap = new HashMap<String, Object>();
//                request.getSession().setAttribute(user.getUserid().toString(),user.getUserid().toString());
                result.put("success",true);
                //数据库中为null时，则返回""
                map.put("userId",user.getUserid() == null?"":user.getUserid());
                map.put("icon", user.getIcon() == null ? "" : user.getIcon());
                map.put("name", user.getRealname() == null ? "" : user.getRealname());
                map.put("sex", user.getSex() == null ? "" : user.getSex());
                map.put("position", user.getPosition() == null ? "" : user.getPosition());
                map.put("power", user.getPower() == null ? "" : user.getPower());
                map.put("phoneNumber", user.getPhonenumber() == null ? "" : user.getPhonenumber());
                map.put("address", user.getAddress() == null ? "" : user.getAddress());
                map.put("description", user.getDescription() == null ? "" : user.getDescription());
                result.put("person",map);
                String token = jwtTokenUtil.generateToken(user);
                tokenMap.put("token","Bearer "+token);
                result.put("auth",tokenMap);
                return StatusUtil.querySuccess(result);
            } else {
                return StatusUtil.error("","该用户不存在！");
            }
        } else {
            return StatusUtil.error("","账号或密码错误！");
        }
    }


    /**
     * 退出登录 将username从session中移除
     * @param userId
     * @param request
     * @return
     */
    @RequestMapping(value = "/rest/token/{userId}",method = RequestMethod.GET)
    public ResponseEntity<?> logout(@PathVariable("userId") Integer userId,HttpServletRequest request){
        String username = String.valueOf(userId.intValue());
//        request.getSession().removeAttribute(username);
        return StatusUtil.updateOk();
    }


}
