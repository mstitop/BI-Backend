package cn.edu.dbsi.controller;

import cn.edu.dbsi.interceptor.LoginRequired;
import cn.edu.dbsi.model.CubeInfo;
import cn.edu.dbsi.service.CubeInfoServiceI;
import cn.edu.dbsi.util.StatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getCubes(@RequestParam Integer page,@RequestParam Integer size) {
        List<CubeInfo> cubes = cubeInfoServiceI.getCubes();
        int start = (page - 1)*size;
        if (cubes != null) {
            Map<String,Object> result = new HashMap<String, Object>();
            Map<String,Object> map = new HashMap<String, Object>();
            Map<String,Object> pageMap = new HashMap<String, Object>();
            int pages = (cubes.size())/size+1;
            map.put("total",cubes.size());
            map.put("pages",pages);
            pageMap.put("start",start);
            pageMap.put("size",size);
            result.put("pagination",map);
            List<CubeInfo> cubeByPage = cubeInfoServiceI.getCubeInfoByPage(pageMap);
            if(cubeByPage !=null){
                List<Map<String,Object>> list = new ArrayList<Map<String,Object>>();
                for(CubeInfo cube:cubeByPage){
                    Map<String,Object> cubeMap = new HashMap<String, Object>();
                    cubeMap.put("id",cube.getId());
                    cubeMap.put("name",cube.getName());
                    cubeMap.put("description",cube.getDescription());
                    cubeMap.put("category",cube.getCategory());
                    cubeMap.put("createTime",cube.getCreateTime());
                    if (cube.getFinishTime() != null && !"".equals(cube.getFinishTime())) {
                        cubeMap.put("finishTime",cube.getFinishTime());
                    }else {
                        cubeMap.put("finishTime","");
                    }
                    cubeMap.put("status",cube.getStatus());
                    cubeMap.put("progress",cube.getProgress());
                    list.add(cubeMap);
                }
                result.put("cubes",list);
                return StatusUtil.querySuccess(result);
            }else {
                return StatusUtil.error("","此页为空");
            }
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
            map.put("progress",cubeInfo.getProgress());
            if (cubeInfo.getFinishTime() != null && !"".equals(cubeInfo.getFinishTime())) {
                map.put("finishTime",cubeInfo.getFinishTime());
            }else {
                map.put("finishTime","");
            }
            return StatusUtil.querySuccess(map);
        }else {
            return StatusUtil.error("","不存在此Id的cube信息！");
        }
    }


}
