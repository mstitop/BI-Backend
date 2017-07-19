package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.OlapStoryboardElementInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("OlapStoryboardElementInfoMapper")
public interface OlapStoryboardElementInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OlapStoryboardElementInfo record);

    int insertSelective(OlapStoryboardElementInfo record);

    OlapStoryboardElementInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OlapStoryboardElementInfo record);

    int updateByPrimaryKey(OlapStoryboardElementInfo record);

    List<OlapStoryboardElementInfo> selectByStoryBoardId(Integer storyBoardId);

    int updateIsDeleteByPrimaryKey(OlapStoryboardElementInfo olapStoryboardElementInfo);
}