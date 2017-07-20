package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.OlapStoryboardInfoMapper;
import cn.edu.dbsi.model.OlapStoryboardInfo;
import cn.edu.dbsi.service.OlapStoryboardInfoServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 郭世明 on 2017/7/17.
 */
@Service("olapStoryboardInfoServiceImpl")
public class OlapStoryboardInfoServiceImpl implements OlapStoryboardInfoServiceI {
    @Autowired
    private OlapStoryboardInfoMapper olapStoryboardInfoMapper;

    public List<OlapStoryboardInfo> getAllOlapStoryboardInfo() {
        return olapStoryboardInfoMapper.selectAllStoryboardInfo();
    }

    public int getLastOlapStoryboardInfoPrimaryKey() {
        return olapStoryboardInfoMapper.selectLastPrimaryKey();
    }

    public int addOlapStoryboardInfo(OlapStoryboardInfo olapStoryboardInfo) {
        return olapStoryboardInfoMapper.insert(olapStoryboardInfo);
    }

    public int deleteOlapStoryboardInfo(Integer id) {
        return olapStoryboardInfoMapper.updateIsDeleteByPrimaryKey(id);
    }

    public int updateOlapStoryboardInfo(OlapStoryboardInfo olapStoryboardInfo) {
        return olapStoryboardInfoMapper.updateByPrimaryKeySelective(olapStoryboardInfo);
    }

    public OlapStoryboardInfo getOlapStoryboardInfoById(Integer id) {
        return olapStoryboardInfoMapper.selectByPrimaryKey(id);
    }
}
