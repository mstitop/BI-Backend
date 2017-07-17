package cn.edu.dbsi.controller;

import cn.edu.dbsi.model.DbconnInfo;
import cn.edu.dbsi.service.DbConnectionServiceI;
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
@Controller
@RequestMapping(value = "/{token}")
public class DbConnectionController {

    @Autowired
    private DbConnectionServiceI dbConnectionServiceI;


    /**
     * 获取数据库连接信息
     *
     * @param token 用于验证是否为可靠用户
     * @return
     */
    @RequestMapping(value = "/ds-conns", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getDbConnInfo(@PathVariable("token") Integer token) {
        if (token != null && token == 000) {
            List<DbconnInfo> list = dbConnectionServiceI.getDbConnInfo();
            return new ResponseEntity<Object>(list, HttpStatus.OK);
        } else {
            return StatusUtil.error("", "");
        }
    }

    /**
     * 增加数据库连接信息
     *
     * @param token
     * @param dbconnInfo
     * @return
     */
    @RequestMapping(value = "/ds-conns", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addDbConnInfo(@PathVariable("token") Integer token, @RequestBody DbconnInfo dbconnInfo) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (token != null && token == 000) {
            if (dbconnInfo != null && "Oracle".equals(dbconnInfo.getCategory()))
                dbconnInfo.setJdbcname("oracle.jdbc.driver.OracleDriver");
            else if (dbconnInfo != null && "Mysql".equals(dbconnInfo.getCategory()))
                dbconnInfo.setJdbcname("com.mysql.jdbc.Driver");
            dbconnInfo.setIsdelete("0");
            int tag = dbConnectionServiceI.addDbConnInfo(dbconnInfo);
            if (tag == 1) {
                return StatusUtil.updateOk();
            } else {
                return StatusUtil.error("", "");
            }
        } else {
            return StatusUtil.error("","Unauthorized");
        }
    }

    /**
     * 更新信息
     *
     * @param token
     * @param dbconnInfo
     * @return
     */
    @RequestMapping(value = "/ds-conns/{dsConnsId}", method = RequestMethod.PUT)
    @ResponseBody
    public ResponseEntity<?> updateDbConnInfo(@PathVariable("token") Integer token, @PathVariable("dsConnsId") Integer id, @RequestBody DbconnInfo dbconnInfo) {
        Map<String, Object> map = new HashMap<String, Object>();
        dbconnInfo.setId(id);
        if (token != null && token == 000) {
            int tag = dbConnectionServiceI.updateDbConnInfo(dbconnInfo);
            if (tag == 1) {
                return StatusUtil.updateOk();
            } else {
                return StatusUtil.error("", "");
            }

        } else {
            return StatusUtil.error("", "Unauthorized");
        }
    }

    /**
     * 删除一条连接信息
     *
     * @param token
     * @param id
     * @return
     */
    @RequestMapping(value = "/ds-conns/{dsConnsId}", method = RequestMethod.DELETE)
    @ResponseBody
    public ResponseEntity<?> deleteDbConnInfo(@PathVariable("token") Integer token, @PathVariable("dsConnsId") Integer id) {
        Map<String, Object> map = new HashMap<String, Object>();
        if (token != null && token == 000) {
            int tag = dbConnectionServiceI.deleteDbConnInfo(id);
            if (tag == 1) {
                return StatusUtil.updateOk();
            } else {
                return StatusUtil.error("", "");
            }
        } else {
            return StatusUtil.error("", "Unauthorized");
        }
    }
}
