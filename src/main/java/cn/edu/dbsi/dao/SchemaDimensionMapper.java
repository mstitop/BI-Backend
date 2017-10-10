package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.SchemaDimension;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("schemaDimensionMapper")
public interface SchemaDimensionMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SchemaDimension record);

    int insertSelective(SchemaDimension record);

    SchemaDimension selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SchemaDimension record);

    int updateByPrimaryKey(SchemaDimension record);

    int selectSchemaDimensionLastPrimaryKey();
    List<SchemaDimension> selectAllDimensionBySchemaId(Integer schemaId);
}