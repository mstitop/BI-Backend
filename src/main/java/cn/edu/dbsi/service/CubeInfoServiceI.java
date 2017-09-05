package cn.edu.dbsi.service;

import cn.edu.dbsi.model.CubeInfo;

import java.util.List;

/**
 * Created by 郭世明 on 2017/9/2.
 */
public interface CubeInfoServiceI {
    int addCubeInfo(CubeInfo cubeInfo);

    int selectLastCubeInfoPrimaryKey();

    int updateCubeInfoByPrimaryKey(CubeInfo cubeInfo);

    List<CubeInfo> getCubes();

    CubeInfo getCubeById(Integer id);
}
