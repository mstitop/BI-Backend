package cn.edu.dbsi.service;

import cn.edu.dbsi.model.OlapStoryboardInfo;

import java.util.List;

/**
 * Created by 郭世明 on 2017/7/17.
 */
public interface OlapStoryboardInfoServiceI {

    List<OlapStoryboardInfo> getAllOlapStoryboardInfo();

    int getLastOlapStoryboardInfoPrimaryKey();

    int addOlapStoryboardInfo(OlapStoryboardInfo olapStoryboardInfo);

    int deleteOlapStoryboardInfo(Integer id);

    int updateOlapStoryboardInfo(OlapStoryboardInfo olapStoryboardInfo);

    OlapStoryboardInfo getOlapStoryboardInfoById(Integer id);
}
