package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.SaikuMDXMapper;
import cn.edu.dbsi.model.SaikuMDX;
import cn.edu.dbsi.service.SaikuMDXServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 郭世明 on 2017/9/28.
 */
@Service("SaikuMDXServiceImpl")
public class SaikuMDXServiceImpl implements SaikuMDXServiceI {

    @Autowired
    private SaikuMDXMapper saikuMDXMapper;

    public int addSaikuMax(SaikuMDX saikuMDX) {
        return saikuMDXMapper.insert(saikuMDX);
    }

    public int selectLastPrimaryKey(){
        return saikuMDXMapper.selectSaikuMdxLastPrimaryKey();
    }

    public SaikuMDX selectByPrimaryKey(Integer id){return saikuMDXMapper.selectByPrimaryKey(id); }
}
