package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.CubeInfo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("CubeInfoMapper")
public interface CubeInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(CubeInfo record);

    int insertSelective(CubeInfo record);

    CubeInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(CubeInfo record);

    int updateByPrimaryKey(CubeInfo record);

    int selectCubeInfoLastPrimaryKey();

    List<CubeInfo> selectAllCubeInfo();

    List<CubeInfo> selectCubeInfoByPage(Map<String,Object> map);

    int selectLastPrimaryKey();


    List<String> selectCubeNames();

    int selectSchemaIdByCubeName(String cubeName);

}