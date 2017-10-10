package cn.edu.dbsi.service;

import cn.edu.dbsi.model.CubeInfo;

import java.util.List;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/9/2.
 */
public interface CubeInfoServiceI {
    int addCubeInfo(CubeInfo cubeInfo);

    int selectLastCubeInfoPrimaryKey();

    int updateCubeInfoByPrimaryKey(CubeInfo cubeInfo);

    List<CubeInfo> getCubes();

    CubeInfo getCubeById(Integer id);

    List<CubeInfo> getCubeInfoByPage(Map<String,Object> map);

    int getLastCubeInfoId();

<<<<<<< HEAD
    List<String> getCubesNames();
=======
    int getSchemaIdByCubeName(String cubeName);
>>>>>>> 011bbc2d150ba8fa379178ed26e75a2bf7121015
}
