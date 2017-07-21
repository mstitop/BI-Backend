package cn.edu.dbsi.controller;

import cn.edu.dbsi.interceptor.LoginRequired;
import cn.edu.dbsi.model.DbconnInfo;
import cn.edu.dbsi.service.DbConnectionServiceI;
import cn.edu.dbsi.util.DBUtils;
import cn.edu.dbsi.util.StatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/6/26.
 */
@LoginRequired
@RestController
@RequestMapping(value = "/rest")
public class SourceDBController {
    @Autowired
    private DbConnectionServiceI dbConnectionServiceI;

    /**
     * 查询源数据库
     *
     * @return
     */
    @RequestMapping(value = "/source-dbs", method = RequestMethod.GET)
    public ResponseEntity<?> getSoureDbInfo() {
        List<DbconnInfo> list = dbConnectionServiceI.getDbConnInfo();
        if (list != null && list.size() > 0) {
            List<Map<String, Object>> dbsList = new ArrayList<Map<String, Object>>();
            for (DbconnInfo dbconnInfo : list) {
                Map<String, Object> dbs = new HashMap<String, Object>();
                dbs.put("id", dbconnInfo.getId());
                dbs.put("name", dbconnInfo.getName());
                dbs.put("category", dbconnInfo.getCategory());
                //获取源数据库里的所有表
                List<Map<String, Object>> tablesList;
                if ("Oracle".equals(dbconnInfo.getCategory())) {
                    if (DBUtils.testConn(dbconnInfo) == 1) {
                        tablesList = DBUtils.list2(dbconnInfo, "select TABLE_NAME from user_tables", null);
                    } else {
                        return StatusUtil.error("40003", "相关JDBC链接无法接入");
                    }
                } else {
                    if (DBUtils.testConn(dbconnInfo) == 1) {
                        tablesList = DBUtils.list2(dbconnInfo, "show tables", null);
                    } else {
                        return StatusUtil.error("40003", "相关JDBC链接无法接入");
                    }
                }
                if (tablesList != null)
                    dbs.put("tables", tablesList);
                else
                    dbs.put("tables", "");
                dbsList.add(dbs);
            }
            return new ResponseEntity<Object>(dbsList, HttpStatus.OK);
        } else {
            return StatusUtil.error("", "查询源数据库失败");
        }
    }

    @RequestMapping(value = "/source-dbs/{dbId}/table/{tableName}", method = RequestMethod.GET)
    public ResponseEntity<?> getSoureDbInfo(@PathVariable("dbId") Integer dbid, @PathVariable("tableName") String tableName) {
        StringBuilder sql = new StringBuilder();
        DbconnInfo dbconnInfo = dbConnectionServiceI.getDbconnInfoById(dbid);
        if ("Mysql".equals(dbconnInfo.getCategory())) {
            sql.append("select COLUMN_NAME name,DATA_TYPE type from information_schema.COLUMNS where table_name = ").append("'").append(tableName).append("'");
            //获取数据库名
            String[] database = dbconnInfo.getUrl().split("/");
            sql.append(" AND table_schema = ").append("'").append(database[3]).append("'");
        } else {
            //本身带有双引号的String，使用转义字符
            sql.append("select distinct column_name \"name\",data_type \"type\" from dba_tab_columns where table_name = ").append("'").append(tableName).append("'");
        }
        if (dbconnInfo != null) {
            List<Map<String, Object>> columnList = DBUtils.list(dbconnInfo, sql.toString(), null);
            return StatusUtil.querySuccess(columnList);
        } else {
            return StatusUtil.error("", "");
        }
    }

}
