package cn.edu.dbsi.controller;

import cn.edu.dbsi.model.BusinessPackageGroup;
import cn.edu.dbsi.service.BusinessPackageGroupServiceI;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/6/27.
 */

@Controller
@RequestMapping(value = "/{token}")
public class BusinessPackageGroupController {

    @Autowired
    private BusinessPackageGroupServiceI businessPackageGroupServiceI;

    /**
     * 增加分组信息
     * @param token
     * @param json
     * @return
     */
    @RequestMapping(value = "/businessPackageGroup", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addBusinessPackage(@PathVariable("token") Integer token, @RequestBody Map<String, Object> json) {
        Map<String, Object> map = new HashMap<String, Object>();
        BusinessPackageGroup businessPackageGroup = new BusinessPackageGroup();
        JSONObject obj = new JSONObject(json);
        String groupName = obj.getString("name");
        businessPackageGroup.setName(groupName);
        businessPackageGroup.setIsdelete("0");
        int tag = businessPackageGroupServiceI.saveBusinessPackageGroup(businessPackageGroup);
        if (tag == 1) {
            map.put("result", 1);
        } else {
            map.put("result", 0);
            map.put("error", "保存失败");
        }
        return map;
    }

}
