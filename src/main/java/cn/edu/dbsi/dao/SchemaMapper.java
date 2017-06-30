package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.Schema;
import org.springframework.stereotype.Repository;

@Repository("schemaMapper")
public interface SchemaMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Schema record);

    int insertSelective(Schema record);

    Schema selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Schema record);

    int updateByPrimaryKey(Schema record);

    int selectSchemaLastPrimaryKey();
}