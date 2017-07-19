package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.OlapStoryboardElementInfoMapper;
import cn.edu.dbsi.model.OlapStoryboardElementInfo;
import cn.edu.dbsi.service.OlapStoryboardElementInfoServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 郭世明 on 2017/7/17.
 */
@Service("olapStoryboardElementInfoServiceImpl")
public class OlapStoryboardElementInfoServiceImpl implements OlapStoryboardElementInfoServiceI {

    @Autowired
    private OlapStoryboardElementInfoMapper olapStoryboardElementInfoMapper;

    public List<OlapStoryboardElementInfo> getOlapStoryboardElementInfoByStoryBoardId(Integer storyBoardId) {
        return olapStoryboardElementInfoMapper.selectByStoryBoardId(storyBoardId);
    }

    public int addOlapStoryboardElementInfo(OlapStoryboardElementInfo olapStoryboardElementInfo) {
        return olapStoryboardElementInfoMapper.insert(olapStoryboardElementInfo);
    }

    public int updateOlapStoryboardElementInfo(OlapStoryboardElementInfo olapStoryboardElementInfo) {
        return olapStoryboardElementInfoMapper.updateByPrimaryKeySelective(olapStoryboardElementInfo);
    }

    public int deleteOlapStoryboardElementInfo(OlapStoryboardElementInfo olapStoryboardElementInfo) {
        return olapStoryboardElementInfoMapper.updateIsDeleteByPrimaryKey(olapStoryboardElementInfo);
    }
}
