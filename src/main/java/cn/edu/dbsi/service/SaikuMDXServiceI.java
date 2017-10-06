package cn.edu.dbsi.service;

import cn.edu.dbsi.model.SaikuMDX;

/**
 * Created by 郭世明 on 2017/9/28.
 */
public interface SaikuMDXServiceI {

    int addSaikuMax(SaikuMDX saikuMDX);
    int selectLastPrimaryKey();
    SaikuMDX selectByPrimaryKey(Integer id);
}
