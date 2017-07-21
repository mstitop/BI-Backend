package cn.edu.dbsi.controller;

import cn.edu.dbsi.interceptor.LoginRequired;
import cn.edu.dbsi.model.BusinessPackage;
import cn.edu.dbsi.model.BusinessPackageGroup;
import cn.edu.dbsi.model.DbBusinessPackage;
import cn.edu.dbsi.model.DbconnInfo;
import cn.edu.dbsi.service.BusinessPackageGroupServiceI;
import cn.edu.dbsi.service.BusinessPackageServiceI;
import cn.edu.dbsi.service.DbBusinessPackageServiceI;
import cn.edu.dbsi.service.DbConnectionServiceI;
import cn.edu.dbsi.util.StatusUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/6/23.
 */
@LoginRequired
@Controller
@RequestMapping(value = "/rest")
public class BusinessPackageController {

    @Autowired
    private BusinessPackageServiceI businessPackageServiceI;

    @Autowired
    private DbBusinessPackageServiceI dbBusinessPackageServiceI;

    @Autowired
    private DbConnectionServiceI dbConnectionServiceI;

    @Autowired
    private BusinessPackageGroupServiceI businessPackageGroupServiceI;

    /**
     * 查询所有业务包信息
     *
     * @return
     */
    @RequestMapping(value = "/business-package-groups", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getBusinessPackageGroupById() {
        List<BusinessPackageGroup> list = businessPackageServiceI.getBusinessPackageGroup();
        if (list != null)
            return StatusUtil.querySuccess(list);
        else
            return StatusUtil.error("", "查询所有业务包失败");
    }

    /**
     * 新增业务包信息
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/business-package", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addBusinessPackage(@RequestBody Map<String, Object> json) {
        BusinessPackage businessPackage = new BusinessPackage();
        DbBusinessPackage dbBusinessPackage = new DbBusinessPackage();
        //System.out.println(json.toString());
        JSONObject obj = new JSONObject(json);
        String name = obj.getString("name");
        String groupid = String.valueOf(obj.getInt("groupid"));
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
            String dbid = String.valueOf(temp.getInt("id"));
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
            return StatusUtil.updateOk();
        } else {
            return StatusUtil.error("", "数据包保存失败");
        }
    }

    /**
     * 更新数据包信息
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/business-package/{businessPackageId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> updateBusinessPackage(@RequestBody Map<String, Object> json, @PathVariable("businessPackageId") Integer businessPackageId) {
        BusinessPackage businessPackage = new BusinessPackage();
        DbBusinessPackage dbBusinessPackage = new DbBusinessPackage();
        JSONObject obj = new JSONObject(json);
        String bpid = String.valueOf(obj.getInt("id"));
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
            String dbid = String.valueOf(temp.getInt("id"));
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
            return StatusUtil.updateOk();
        } else {
            return StatusUtil.error("", "数据包更新失败");
        }
    }

    /**
     * 删除业务包
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/business-package/{id}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteBusinessPackageById(@PathVariable("id") Integer id) {
        BusinessPackage businessPackage = new BusinessPackage();
        businessPackage.setId(id);
        int tag = businessPackageServiceI.deleteBusinessPackage(businessPackage);
        if (tag == 1) {
            return StatusUtil.updateOk();
        } else {
            return StatusUtil.error("", "数据包删除失败");
        }
    }


    /**
     * 指定业务包信息查询
     *
     * @param packageid
     * @return
     */
    @RequestMapping(value = "/business-package/{packageid}", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getBusinessPackageById(@PathVariable("packageid") Integer packageid) {
        DbconnInfo dbconnInfo;
        Map<String, Object> bpmap = new HashMap<String, Object>();
        Map<String, Object> groupmap = new HashMap<String, Object>();
        List<Map<String, Object>> dbs = new ArrayList<Map<String, Object>>();
        BusinessPackage businessPackage = businessPackageServiceI.getBusinessPackageById(packageid);
        if (businessPackage != null) {
            BusinessPackageGroup businessPackageGroup = businessPackageGroupServiceI.getBusinessPackageGroupById(businessPackage.getGroupid());
            if (businessPackageGroup != null) {
                groupmap.put("id", businessPackageGroup.getId());
                groupmap.put("name", businessPackageGroup.getName());
                bpmap.put("id", businessPackage.getId());
                bpmap.put("name", businessPackage.getName());
                bpmap.put("group", groupmap);
                List<DbBusinessPackage> dbBusinessPackage = dbBusinessPackageServiceI.getDbBusinessPackagesByBpid(businessPackage.getId());
                if (dbBusinessPackage != null) {
                    for (DbBusinessPackage item : dbBusinessPackage) {
                        int dbid = item.getDbid();
                        dbconnInfo = dbConnectionServiceI.getDbconnInfoById(dbid);
                        Map<String, Object> temp = new HashMap<String, Object>();
                        temp.put("id", dbconnInfo.getId());
                        temp.put("name", dbconnInfo.getName());
                        temp.put("category", dbconnInfo.getCategory());
                        if (item.getTablename() != null && !"".equals(item.getTablename())) {
                            String[] tableNames = item.getTablename().split(",");
                            List<Map<String, Object>> tables = new ArrayList<Map<String, Object>>();
                            for (int i = 0; i < tableNames.length; i++) {
                                Map<String, Object> temp2 = new HashMap<String, Object>();
                                temp2.put("id", item.getId());
                                temp2.put("name", tableNames[i]);
                                tables.add(temp2);
                            }
                            temp.put("tables", tables);
                            dbs.add(temp);
                        } else {
                            temp.put("tables", "");
                            dbs.add(temp);
                        }
                    }
                    bpmap.put("dbs", dbs);
                    return StatusUtil.querySuccess(bpmap);
                } else {
                    bpmap.put("dbs", "");
                    return StatusUtil.querySuccess(bpmap);
                }
            } else {
                return StatusUtil.error("", "此业务包不属于任何分组");
            }
        } else {
            return StatusUtil.error("", "不存在此业务包");
        }
    }

}
