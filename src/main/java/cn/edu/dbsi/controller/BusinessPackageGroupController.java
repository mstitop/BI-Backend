package cn.edu.dbsi.controller;

import cn.edu.dbsi.interceptor.LoginRequired;
import cn.edu.dbsi.model.BusinessPackageGroup;
import cn.edu.dbsi.service.BusinessPackageGroupServiceI;
import cn.edu.dbsi.util.StatusUtil;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/6/27.
 */
@LoginRequired
@Controller
@RequestMapping(value = "/rest")
public class BusinessPackageGroupController {

    @Autowired
    private BusinessPackageGroupServiceI businessPackageGroupServiceI;

    /**
     * 增加分组信息
     * @param json
     * @return
     */
    @RequestMapping(value = "/business-package-group", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addBusinessPackage( @RequestBody Map<String, Object> json) {
        BusinessPackageGroup businessPackageGroup = new BusinessPackageGroup();
        JSONObject obj = new JSONObject(json);
        String groupName = obj.getString("name");
        businessPackageGroup.setName(groupName);
        businessPackageGroup.setIsdelete("0");
        int tag = businessPackageGroupServiceI.saveBusinessPackageGroup(businessPackageGroup);
        if (tag == 1) {
           return StatusUtil.updateOk();
        } else {
            return StatusUtil.error("","增加分组失败");
        }
    }

    /**
     * 重命名指定业务包分组
     * @param json
     * @return
     */
    @RequestMapping(value = "/business-package-group/{businessPackageGroupId}",method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> updateBusinessPackageGroupName(@RequestBody Map<String,Object> json,@PathVariable("businessPackageGroupId") Integer businessPackageGroupId){
        BusinessPackageGroup businessPackageGroup = new BusinessPackageGroup();
        JSONObject obj = new JSONObject(json);
        int id = obj.getInt("id");
        String name = obj.getString("name");
        businessPackageGroup.setId(id);
        businessPackageGroup.setName(name);
        int tag =businessPackageGroupServiceI.upadateBusinessPackageGroupName(businessPackageGroup);
        if(tag == 1){
            return StatusUtil.updateOk();
        }else{
            return StatusUtil.error("","重命名业务包分组失败");
        }
    }

    @RequestMapping(value = "/business-package-group/{businessPackageGroupId}",method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteBusinessPackageGroup(@PathVariable("businessPackageGroupId") Integer businessPackageGroupId){
        int tag =businessPackageGroupServiceI.deleteBusinessPackageGroup(businessPackageGroupId);
        if(tag == 1){
            return StatusUtil.updateOk();
        }else{
            return StatusUtil.error("","删除业务包分组失败");
        }
    }

}
