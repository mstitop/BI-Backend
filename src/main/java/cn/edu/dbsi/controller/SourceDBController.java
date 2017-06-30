package cn.edu.dbsi.controller;

import cn.edu.dbsi.model.DbconnInfo;
import cn.edu.dbsi.service.DbConnectionServiceI;
import cn.edu.dbsi.util.DBUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/6/26.
 */

@Controller
@RequestMapping(value = "/{token}")
public class SourceDBController {
    @Autowired
    private DbConnectionServiceI dbConnectionServiceI;

    /**
     * 查询源数据库
     *
     * @param token
     * @return
     */
    @RequestMapping(value = "/sourcedbs", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getSoureDbInfo(@PathVariable("token") Integer token) {
        Map<String, Object> result = new HashMap<String, Object>();
        List<DbconnInfo> list = dbConnectionServiceI.getDbConnInfo();
        if (list != null && list.size() > 0) {
            result.put("result", 1);
            List<Map<String, Object>> dbsList = new ArrayList<Map<String, Object>>();
            for (DbconnInfo dbconnInfo : list) {
                Map<String, Object> dbs = new HashMap<String, Object>();
                dbs.put("id", dbconnInfo.getId());
                dbs.put("name", dbconnInfo.getName());
                dbs.put("category", dbconnInfo.getCategory());
                //获取源数据库里的所有表
                List<Map<String, Object>> tablesList;
                if ("Oracle".equals(dbconnInfo.getCategory())) {
                    tablesList = DBUtils.list2(dbconnInfo, "select TABLE_NAME from user_tables", null);
                } else {
                    tablesList = DBUtils.list2(dbconnInfo, "show tables", null);
                }
                if (tablesList != null)
                    dbs.put("tables", tablesList);
                else
                    dbs.put("tables", "");
                dbsList.add(dbs);
            }
            result.put("dbs", dbsList);
        } else {
            result.put("result", 0);
            result.put("dbs", "");
            result.put("error", "没有相关信息");
        }
        return result;
    }

    @RequestMapping(value = "/tableOfSourceDB/{dbid}/{tableName}", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getSoureDbInfo(@PathVariable("token") Integer token, @PathVariable("dbid") Integer dbid, @PathVariable("tableName") String tableName) {
        Map<String, Object> result = new HashMap<String, Object>();
        StringBuilder sql = new StringBuilder();
        DbconnInfo dbconnInfo = dbConnectionServiceI.getDbconnInfoById(dbid);
        if ("Mysql".equals(dbconnInfo.getCategory())) {
            sql.append("select COLUMN_NAME name,DATA_TYPE type from information_schema.COLUMNS where table_name = ").append("'").append(tableName).append("'");
            //获取数据库名
            String[] database = dbconnInfo.getUrl().split("/");
            sql.append(" AND table_schema = ").append("'").append(database[3]).append("'");
        }else {
            sql.append("select distinct column_name from dba_tab_columns where table_name = ").append("'").append(tableName).append("'");
        }
        System.out.println(sql.toString());
        if (dbconnInfo != null) {
            result.put("result", 1);
            List<Map<String, Object>> columnList = DBUtils.list(dbconnInfo, sql.toString(), null);
            result.put("fields", columnList);
        } else {
            result.put("result", 0);
            result.put("error", "查询失败");
            result.put("fields", "");
        }
        return result;
    }

}
