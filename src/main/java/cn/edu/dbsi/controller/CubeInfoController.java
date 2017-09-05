package cn.edu.dbsi.controller;

import cn.edu.dbsi.interceptor.LoginRequired;
import cn.edu.dbsi.model.CubeInfo;
import cn.edu.dbsi.service.CubeInfoServiceI;
import cn.edu.dbsi.util.StatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
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
 * Created by 郭世明 on 2017/9/4.
 */
@LoginRequired
@Controller
@RequestMapping(value = "/rest")
public class CubeInfoController {

    @Autowired
    private CubeInfoServiceI cubeInfoServiceI;

    @RequestMapping(value = "/cubes", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getCubes() {
        List<CubeInfo> cubes = cubeInfoServiceI.getCubes();
        if (cubes != null) {
            return StatusUtil.querySuccess(cubes);
        }else{
            return StatusUtil.error("","内容为空！");
        }
    }

    @RequestMapping(value = "/cube/{cubeId}/status", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getCubeById(@PathVariable("cubeId") Integer cubeId) {
        CubeInfo cubeInfo = cubeInfoServiceI.getCubeById(cubeId);
        if(cubeInfo != null){
            Map<String,Object> map = new HashMap<String, Object>();
            map.put("status",cubeInfo.getStatus());
            return StatusUtil.querySuccess(map);
        }else {
            return StatusUtil.error("","不存在此Id的cube信息！");
        }
    }


}
