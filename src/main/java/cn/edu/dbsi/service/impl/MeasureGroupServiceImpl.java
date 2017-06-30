package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.SchemaMeasureGroupMapper;
import cn.edu.dbsi.model.SchemaMeasureGroup;
import cn.edu.dbsi.service.MeasureGroupServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 郭世明 on 2017/6/29.
 */
@Service("measureGroupServiceImpl")
public class MeasureGroupServiceImpl implements MeasureGroupServiceI {

    @Autowired
    private SchemaMeasureGroupMapper schemaMeasureGroupMapper;

    public int addMeasureGroup(SchemaMeasureGroup schemaMeasureGroup) {
        return schemaMeasureGroupMapper.insert(schemaMeasureGroup);
    }

    public int getLastMeasureGroupId() {
        return schemaMeasureGroupMapper.selectSchemaMeasureGroupLastPrimaryKey();
    }
}
