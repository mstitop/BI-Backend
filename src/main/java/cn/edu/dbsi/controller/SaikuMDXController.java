package cn.edu.dbsi.controller;

import cn.edu.dbsi.model.BusinessPackageGroup;
import cn.edu.dbsi.model.SaikuMDX;
import cn.edu.dbsi.service.SaikuMDXServiceI;
import cn.edu.dbsi.util.StatusUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Date;
import java.util.List;

/**
 * Created by 郭世明 on 2017/9/28.
 */
@Controller
@RequestMapping(value = "/add")
public class SaikuMDXController {

    @Autowired
    private SaikuMDXServiceI saikuMDXServiceI;

    @RequestMapping(value = "/mdx", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> getBusinessPackageGroupById(@RequestParam String mdx) {
        SaikuMDX saikuMDX = new SaikuMDX();
        saikuMDX.setMdx(mdx);
        saikuMDX.setExecuteTime(new Date());
        int tag = saikuMDXServiceI.addSaikuMax(saikuMDX);
        if(tag == 1){
            return StatusUtil.updateOk();
        }else {
            return StatusUtil.error("","保存mdx失败！");
        }
    }
}
