package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.SchemaMeasure;

public interface SchemaMeasureMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SchemaMeasure record);

    int insertSelective(SchemaMeasure record);

    SchemaMeasure selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SchemaMeasure record);

    int updateByPrimaryKey(SchemaMeasure record);
}