package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.SchemaMeasureGroup;
import org.springframework.stereotype.Repository;

@Repository("schemaMeasureGroupMapper")
public interface SchemaMeasureGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SchemaMeasureGroup record);

    int insertSelective(SchemaMeasureGroup record);

    SchemaMeasureGroup selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SchemaMeasureGroup record);

    int updateByPrimaryKey(SchemaMeasureGroup record);

    int selectSchemaMeasureGroupLastPrimaryKey();
}