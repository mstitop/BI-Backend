package cn.edu.dbsi.controller;

import cn.edu.dbsi.model.BusinessPackage;
import cn.edu.dbsi.model.BusinessPackageGroup;
import cn.edu.dbsi.service.BusinessPackageServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/6/23.
 */
@Controller
@RequestMapping(value = "/{token}")
public class BusinessPackageController {

    @Autowired
    private BusinessPackageServiceI businessPackageServiceI;

    @RequestMapping(value = "/businessPackage",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getBusinessPackageGroupById(@PathVariable("token") Integer token){
        Map<String,Object> map = new HashMap<String, Object>();
        if(token!=null&&token==1){
            List<BusinessPackageGroup> list = businessPackageServiceI.getBusinessPackageGroup();
            map.put("result",1);
            map.put("businessGroups",list);
        }else{
            map.put("result",0);
            map.put("error","获取失败");
        }
        return map;

    }
}
