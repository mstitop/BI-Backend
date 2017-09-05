package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.CubeInfoMapper;
import cn.edu.dbsi.model.CubeInfo;
import cn.edu.dbsi.service.CubeInfoServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 郭世明 on 2017/9/2.
 */
@Service
public class CubeInfoServiceImpl implements CubeInfoServiceI {

    @Autowired
    private CubeInfoMapper cubeInfoMapper;

    public int addCubeInfo(CubeInfo cubeInfo) {
        return cubeInfoMapper.insert(cubeInfo);
    }

    public int selectLastCubeInfoPrimaryKey() {
        return cubeInfoMapper.selectCubeInfoLastPrimaryKey();
    }

    public int updateCubeInfoByPrimaryKey(CubeInfo cubeInfo) {
        return cubeInfoMapper.updateByPrimaryKeySelective(cubeInfo);
    }

    public List<CubeInfo> getCubes() {
        return cubeInfoMapper.selectAllCubeInfo();
    }

    public CubeInfo getCubeById(Integer id) {
        return cubeInfoMapper.selectByPrimaryKey(id);
    }
}
