package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.SchemaDimension;
import org.springframework.stereotype.Repository;

@Repository("schemaDimensionMapper")
public interface SchemaDimensionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SchemaDimension record);

    int insertSelective(SchemaDimension record);

    SchemaDimension selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SchemaDimension record);

    int updateByPrimaryKey(SchemaDimension record);
}