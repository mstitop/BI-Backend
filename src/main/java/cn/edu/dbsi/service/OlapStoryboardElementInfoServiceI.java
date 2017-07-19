package cn.edu.dbsi.service;

import cn.edu.dbsi.model.OlapStoryboardElementInfo;

import java.util.List;

/**
 * Created by 郭世明 on 2017/7/17.
 */
public interface OlapStoryboardElementInfoServiceI {

    List<OlapStoryboardElementInfo> getOlapStoryboardElementInfoByStoryBoardId(Integer storyBoardId);

    int addOlapStoryboardElementInfo(OlapStoryboardElementInfo olapStoryboardElementInfo);

    int updateOlapStoryboardElementInfo(OlapStoryboardElementInfo olapStoryboardElementInfo);

    int deleteOlapStoryboardElementInfo(OlapStoryboardElementInfo olapStoryboardElementInfo);
}
