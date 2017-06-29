package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.SchemaDimensionMeasure;

public interface SchemaDimensionMeasureMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SchemaDimensionMeasure record);

    int insertSelective(SchemaDimensionMeasure record);

    SchemaDimensionMeasure selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SchemaDimensionMeasure record);

    int updateByPrimaryKey(SchemaDimensionMeasure record);
}