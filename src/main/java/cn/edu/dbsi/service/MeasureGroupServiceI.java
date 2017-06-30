package cn.edu.dbsi.service;

import cn.edu.dbsi.model.SchemaMeasureGroup;

/**
 * Created by 郭世明 on 2017/6/29.
 */
public interface MeasureGroupServiceI {
    int addMeasureGroup(SchemaMeasureGroup schemaMeasureGroup);

    int getLastMeasureGroupId();
}
