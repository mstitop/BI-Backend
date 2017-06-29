package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.SchemaMeasure;
import org.springframework.stereotype.Repository;

@Repository("schemaMeasureMapper")
public interface SchemaMeasureMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SchemaMeasure record);

    int insertSelective(SchemaMeasure record);

    SchemaMeasure selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SchemaMeasure record);

    int updateByPrimaryKey(SchemaMeasure record);
}