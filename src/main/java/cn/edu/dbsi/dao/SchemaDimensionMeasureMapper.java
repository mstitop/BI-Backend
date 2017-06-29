package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.SchemaDimensionMeasure;
import org.springframework.stereotype.Repository;

@Repository("schemaDimensionMeasureMapper")
public interface SchemaDimensionMeasureMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SchemaDimensionMeasure record);

    int insertSelective(SchemaDimensionMeasure record);

    SchemaDimensionMeasure selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SchemaDimensionMeasure record);

    int updateByPrimaryKey(SchemaDimensionMeasure record);
}