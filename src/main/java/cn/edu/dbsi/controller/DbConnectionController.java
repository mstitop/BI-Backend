package cn.edu.dbsi.controller;

import cn.edu.dbsi.model.DbconnInfo;
import cn.edu.dbsi.service.DbConnectionServiceI;
import org.springframework.beans.factory.annotation.Autowired;
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
     * @param token 用于验证是否为可靠用户
     * @return
     */
    @RequestMapping(value = "/dsConns",method = RequestMethod.GET)
    @ResponseBody
    public Map<String,Object> getDbConnInfo(@PathVariable("token") Integer token){
        Map<String,Object> map = new HashMap<String, Object>();
        if(token!=null&&token==1){
            map.put("result",1);
            List<DbconnInfo> list = dbConnectionServiceI.getDbConnInfo();
            map.put("conns",list);
        }else{
            map.put("result",0);
            map.put("error","获取失败");
        }
        return map;
    }

    @RequestMapping(value = "/dsConns",method = RequestMethod.POST)
    @ResponseBody
    public Map<String,Object> addDbConnInfo(@PathVariable("token") Integer token, @RequestBody DbconnInfo dbconnInfo) {
        Map<String, Object> map = new HashMap<String, Object>();
        if(token!=null&&token==1){
            if(dbconnInfo!=null&&"Oracle".equals(dbconnInfo.getCategory()))
                dbconnInfo.setJdbcname("oracle.jdbc.driver.OracleDriver");
            else if(dbconnInfo!=null&&"Mysql".equals(dbconnInfo.getCategory()))
                dbconnInfo.setJdbcname("com.mysql.jdbc.Driver");
            int tag = dbConnectionServiceI.addDbConnInfo(dbconnInfo);
            if(tag==1){
                map.put("result",1);
            }else{
                map.put("result",0);
                map.put("error","保存失败");
            }
        }else{
            map.put("result",0);
            map.put("error","保存失败");
        }
        return map;
    }

    @RequestMapping(value = "/dsConns",method = RequestMethod.PUT)
    @ResponseBody
    public Map<String,Object> updateDbConnInfo(@PathVariable("token") Integer token, @RequestBody DbconnInfo dbconnInfo) {
        Map<String, Object> map = new HashMap<String, Object>();
        if(token!=null&&token==1){
            int tag = dbConnectionServiceI.updateDbConnInfo(dbconnInfo);
            if(tag==1){
                map.put("result",1);
            }else{
                map.put("error","修改失败");
            }

        }else{
            map.put("result",0);
            map.put("error","修改失败");
        }
        return map;
    }

    @RequestMapping(value = "/dsConns/{id}",method = RequestMethod.DELETE)
    @ResponseBody
    public Map<String,Object> deleteDbConnInfo(@PathVariable("token") Integer token,@PathVariable("id") Integer id) {
        Map<String, Object> map = new HashMap<String, Object>();
        if(token!=null&&token==1){
            int tag = dbConnectionServiceI.deleteDbConnInfo(id);
            if(tag==1){
                map.put("result",1);
            }else{
                map.put("result",0);
                map.put("error","修改失败");
            }
        }else{
            map.put("result",0);
            map.put("error","修改失败");
        }
        return map;
    }

}
