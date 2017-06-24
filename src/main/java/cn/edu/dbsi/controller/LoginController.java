package cn.edu.dbsi.controller;

import cn.edu.dbsi.model.User;
import cn.edu.dbsi.service.LoginServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/6/2.
 */
@Controller()
@RequestMapping(value = "/login")
public class LoginController {

    @Autowired
    private LoginServiceI loginServiceI;

    @RequestMapping(value = "")
    @ResponseBody
    public Map<String,Object> checkUser(HttpServletRequest request, HttpServletResponse response){
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        Map<String,Object> map = new HashMap<String, Object>();
        User user = loginServiceI.getUserByUsernameAndPassword(account,password);
        if(user!=null){
            if(user.getIsExist()==1){
                map.put("result",1);
                map.put("icon ",user.getIcon()==null?"":user.getIcon());
                map.put("name",user.getRealname()==null?"":user.getRealname());
                map.put("sex",user.getSex()==null?"":user.getSex());
                map.put("position",user.getPosition()==null?"":user.getPosition());
                map.put("power",user.getPower()==null?"":user.getPower());
                map.put("phoneNumber",user.getPhonenumber()==null?"":user.getPhonenumber());
                map.put("address",user.getAddress()==null?"":user.getAddress());
                map.put("description",user.getDescription()==null?"":user.getDescription());
            }else{
                map.put("result",0);
                map.put("error","用户不存在或密码错误");
            }
        }else{
            map.put("result",0);
            map.put("error","用户不存在或密码错误");
        }
        return map;
    }
}
