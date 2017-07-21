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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/6/22.
 */
@LoginRequired
@Controller
@RequestMapping(value = "/rest")
public class DbConnectionController {

    @Autowired
    private DbConnectionServiceI dbConnectionServiceI;


    /**
     * 获取数据库连接信息
     *
     * @return
     */
    @RequestMapping(value = "/ds-conns", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getDbConnInfo() {
        List<DbconnInfo> list = dbConnectionServiceI.getDbConnInfo();
        if (list != null) {
            return new ResponseEntity<Object>(list, HttpStatus.OK);
        } else {
            return StatusUtil.error("", "获取数据库连接信息失败");
        }
    }

    /**
     * 增加数据库连接信息
     *
     * @param dbconnInfo
     * @return
     */
    @RequestMapping(value = "/ds-conn", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addDbConnInfo(@RequestBody DbconnInfo dbconnInfo) {
            int tag = 0;
            if (dbconnInfo != null && "Oracle".equals(dbconnInfo.getCategory()))
                dbconnInfo.setJdbcname("oracle.jdbc.driver.OracleDriver");
            else if (dbconnInfo != null && "Mysql".equals(dbconnInfo.getCategory()))
                dbconnInfo.setJdbcname("com.mysql.jdbc.Driver");
            dbconnInfo.setIsdelete("0");
            if (DBUtils.testConn(dbconnInfo) == 1) {
                tag = dbConnectionServiceI.addDbConnInfo(dbconnInfo);
            } else if (DBUtils.testConn(dbconnInfo) == -1) {
                return StatusUtil.error("40003", "用户名或密码错误！");
            } else if (DBUtils.testConn(dbconnInfo) == -2) {
                return StatusUtil.error("40003", "此URL无效或不允许接入！");
            }
            if (tag == 1) {
                return StatusUtil.updateOk();
            } else {
                return StatusUtil.error("", "增加数据库连接信息失败");
            }
    }

    /**
     * 更新信息
     *
     * @param dbconnInfo
     * @return
     */
    @RequestMapping(value = "/ds-conn/{dsConnsId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> updateDbConnInfo(@PathVariable("dsConnsId") Integer id, @RequestBody DbconnInfo dbconnInfo) {
        dbconnInfo.setId(id);
            int tag = 0;
            if (dbconnInfo != null && "Oracle".equals(dbconnInfo.getCategory()))
                dbconnInfo.setJdbcname("oracle.jdbc.driver.OracleDriver");
            else if (dbconnInfo != null && "Mysql".equals(dbconnInfo.getCategory()))
                dbconnInfo.setJdbcname("com.mysql.jdbc.Driver");
            if (DBUtils.testConn(dbconnInfo) == 1) {
                tag = dbConnectionServiceI.updateDbConnInfo(dbconnInfo);
            } else if (DBUtils.testConn(dbconnInfo) == -1) {
                return StatusUtil.error("40003", "用户名或密码错误！");
            } else if (DBUtils.testConn(dbconnInfo) == -2) {
                return StatusUtil.error("40003", "此URL无效或不允许接入！");
            }
            if (tag == 1) {
                return StatusUtil.updateOk();
            } else {
                return StatusUtil.error("", "更新失败");
            }
    }

    /**
     * 删除一条连接信息
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/ds-conn/{dsConnsId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteDbConnInfo(@PathVariable("dsConnsId") Integer id) {
            int tag = dbConnectionServiceI.deleteDbConnInfo(id);
            if (tag == 1) {
                return StatusUtil.updateOk();
            } else {
                return StatusUtil.error("", "删除链接失败");
            }
    }


    /**
     * 测试链接是否成功
     *
     * @param dbconnInfo
     * @return
     */
    @RequestMapping(value = "/ds-conn/checking", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> checkDbConnInfo(@RequestBody DbconnInfo dbconnInfo) {
        if (dbconnInfo != null && "Oracle".equals(dbconnInfo.getCategory()))
            dbconnInfo.setJdbcname("oracle.jdbc.driver.OracleDriver");
        else if (dbconnInfo != null && "Mysql".equals(dbconnInfo.getCategory()))
            dbconnInfo.setJdbcname("com.mysql.jdbc.Driver");
        if (DBUtils.testConn(dbconnInfo) == 1) {
            return StatusUtil.updateOk();
        } else if (DBUtils.testConn(dbconnInfo) == -1) {
            return StatusUtil.error("40003", "用户名或密码错误！");
        } else {
            return StatusUtil.error("40003", "此URL无效或不允许接入！");
        }
    }
}
