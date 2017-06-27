package cn.edu.dbsi.controller;

import cn.edu.dbsi.model.BusinessPackage;
import cn.edu.dbsi.model.BusinessPackageGroup;
import cn.edu.dbsi.model.DbBusinessPackage;
import cn.edu.dbsi.model.DbconnInfo;
import cn.edu.dbsi.service.BusinessPackageGroupServiceI;
import cn.edu.dbsi.service.BusinessPackageServiceI;
import cn.edu.dbsi.service.DbBusinessPackageServiceI;
import cn.edu.dbsi.service.DbConnectionServiceI;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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

    @Autowired
    private DbBusinessPackageServiceI dbBusinessPackageServiceI;

    @Autowired
    private DbConnectionServiceI dbConnectionServiceI;

    @Autowired
    private BusinessPackageGroupServiceI businessPackageGroupServiceI;

    @RequestMapping(value = "/businessPackages", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getBusinessPackageGroupById(@PathVariable("token") Integer token) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (token != null && token == 1) {
            List<BusinessPackageGroup> list = businessPackageServiceI.getBusinessPackageGroup();
            map.put("result", 1);
            map.put("businessPackageGroups", list);
        } else {
            map.put("result", 0);
            map.put("error", "获取失败");
        }
        return map;
    }

    /**
     * 新增业务包信息
     *
     * @param token
     * @param json
     * @return
     */
    @RequestMapping(value = "/businessPackage", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addBusinessPackage(@PathVariable("token") Integer token, @RequestBody Map<String, Object> json) {
        Map<String, Object> map = new HashMap<String, Object>();
        BusinessPackage businessPackage = new BusinessPackage();
        DbBusinessPackage dbBusinessPackage = new DbBusinessPackage();
        //System.out.println(json.toString());
        JSONObject obj = new JSONObject(json);
        String name = obj.getString("name");
        String groupid = obj.getString("groupid");
        businessPackage.setName(name);
        businessPackage.setGroupid(Integer.parseInt(groupid));
        businessPackage.setIsdelete("0");
        int tag_one = businessPackageServiceI.saveBusinessPackage(businessPackage);
        int tag_two = 0;
        int bpid = businessPackageServiceI.getLastBusinessPackageId();
        JSONArray dbs = obj.getJSONArray("dbs");
        for (int i = 0; i < dbs.length(); i++) {
            StringBuilder sb = new StringBuilder();
            //获取数组中的每个对象
            JSONObject temp = dbs.getJSONObject(i);
            String dbid = temp.getString("id");
            JSONArray tables = temp.getJSONArray("tables");
            for (int j = 0; j < tables.length(); j++) {
                //获取数组中的每个对象
                JSONObject tablesObj = tables.getJSONObject(j);
                String tempTablesName = tablesObj.getString("name");
                sb.append(tempTablesName + ",");
            }
            dbBusinessPackage.setBpid(bpid);
            dbBusinessPackage.setDbid(Integer.parseInt(dbid));
            dbBusinessPackage.setTablename(sb.toString());
            tag_two = dbBusinessPackageServiceI.addDbBusinessPackage(dbBusinessPackage);
        }
        if (tag_one == 1 && tag_two == 1) {
            map.put("result", 1);
        } else {
            map.put("result", 0);
            map.put("error", "保存失败");
        }
        return map;
    }

    /**
     * 更新数据包信息
     *
     * @param token
     * @param json
     * @return
     */
    @RequestMapping(value = "/businessPackage", method = RequestMethod.PUT)
    @ResponseBody
    public Map<String, Object> updateBusinessPackage(@PathVariable("token") Integer token, @RequestBody Map<String, Object> json) {
        Map<String, Object> map = new HashMap<String, Object>();
        BusinessPackage businessPackage = new BusinessPackage();
        DbBusinessPackage dbBusinessPackage = new DbBusinessPackage();
        JSONObject obj = new JSONObject(json);
        String bpid = obj.getString("id");
        String name = obj.getString("name");
        businessPackage.setId(Integer.parseInt(bpid));
        businessPackage.setName(name);
        int tag_one = businessPackageServiceI.updateBusinessPackage(businessPackage);
        int tag_two = 0;
        JSONArray dbs = obj.getJSONArray("dbs");
        for (int i = 0; i < dbs.length(); i++) {
            StringBuilder sb = new StringBuilder();
            //获取数组中的每个对象
            JSONObject temp = dbs.getJSONObject(i);
            String dbid = temp.getString("id");
            JSONArray tables = temp.getJSONArray("tables");
            for (int j = 0; j < tables.length(); j++) {
                //获取数组中的每个对象
                JSONObject tablesObj = tables.getJSONObject(j);
                String tempTablesName = tablesObj.getString("name");
                sb.append(tempTablesName + ",");
            }
            dbBusinessPackage.setBpid(Integer.parseInt(bpid));
            dbBusinessPackage.setDbid(Integer.parseInt(dbid));
            dbBusinessPackage.setTablename(sb.toString());
            int id = dbBusinessPackageServiceI.selectDbBusinessPackagePrimaryKey(dbBusinessPackage);
            dbBusinessPackage.setId(id);
            tag_two = dbBusinessPackageServiceI.updateDbBusinessPackage(dbBusinessPackage);
        }
        if (tag_one == 1 && tag_two == 1) {
            map.put("result", 1);
        } else {
            map.put("result", 0);
            map.put("error", "更新失败");
        }
        return map;
    }

    /**
     * 假删除业务包
     *
     * @param token
     * @param json
     * @param id
     * @return
     */
    @RequestMapping(value = "/businessPackage/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String, Object> deleteBusinessPackageById(@PathVariable("token") Integer token, @RequestBody Map<String, Object> json, @PathVariable("id") Integer id) {
        Map<String, Object> map = new HashMap<String, Object>();
        BusinessPackage businessPackage = new BusinessPackage();
        businessPackage.setId(id);
        int tag = businessPackageServiceI.deleteBusinessPackage(businessPackage);
        if (tag == 1) {
            map.put("result", 1);
        } else {
            map.put("result", 0);
            map.put("error", "修改失败");
        }
        return map;
    }


    /**
     * 指定业务包信息查询
     * @param token
     * @param packageid
     * @return
     */
    @RequestMapping(value = "/businessPackage/{packageid}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getBusinessPackageById(@PathVariable("token") Integer token, @PathVariable("packageid") Integer packageid) {
        DbconnInfo dbconnInfo;
        Map<String, Object> map = new HashMap<String, Object>();
        Map<String, Object> bpmap = new HashMap<String, Object>();
        Map<String, Object> groupmap = new HashMap<String, Object>();
        List<Map<String,Object>> dbs = new ArrayList<Map<String, Object>>();
        List<Map<String,Object>> tables = new ArrayList<Map<String, Object>>();
        BusinessPackage businessPackage = businessPackageServiceI.getBusinessPackageById(packageid);
        if(businessPackage!= null) {
            BusinessPackageGroup businessPackageGroup = businessPackageGroupServiceI.getBusinessPackageGroupById(businessPackage.getGroupid());
            if(businessPackageGroup!=null) {
                groupmap.put("id", businessPackageGroup.getId());
                groupmap.put("name", businessPackageGroup.getName());
                bpmap.put("id", businessPackage.getId());
                bpmap.put("name", businessPackage.getName());
                bpmap.put("group", groupmap);
                List<DbBusinessPackage> dbBusinessPackage = dbBusinessPackageServiceI.getDbBusinessPackagesByBpid(businessPackage.getId());
                if(dbBusinessPackage!=null) {
                    for (DbBusinessPackage item : dbBusinessPackage) {
                        int dbid = item.getDbid();
                        dbconnInfo = dbConnectionServiceI.getDbconnInfoById(dbid);
                        Map<String, Object> temp = new HashMap<String, Object>();
                        temp.put("id", dbconnInfo.getId());
                        temp.put("name", dbconnInfo.getName());
                        if(item.getTablename()!=null&&!"".equals(item.getTablename())) {
                            String[] tableNames = item.getTablename().split(",");
                            for (int i = 0; i < tableNames.length; i++) {
                                Map<String, Object> temp2 = new HashMap<String, Object>();
                                temp2.put("id", item.getId());
                                temp2.put("name", tableNames[i]);
                                tables.add(temp2);
                            }
                            temp.put("tables", tables);
                            dbs.add(temp);
                        }else{
                            temp.put("tables", "");
                            dbs.add(temp);
                        }
                    }
                    bpmap.put("dbs", dbs);
                    map.put("result",1);
                    map.put("businessPackage",bpmap);
                }
            }
        }else{
            map.put("result",0);
            map.put("businessPackage","");
        }
        return map;
    }

}
