package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.OlapStoryboardInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("OlapStoryboardInfoMapper")
public interface OlapStoryboardInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OlapStoryboardInfo record);

    int insertSelective(OlapStoryboardInfo record);

    OlapStoryboardInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OlapStoryboardInfo record);

    int updateByPrimaryKey(OlapStoryboardInfo record);

    List<OlapStoryboardInfo> selectAllStoryboardInfo();

    int selectLastPrimaryKey();

    int updateIsDeleteByPrimaryKey(Integer id);
}