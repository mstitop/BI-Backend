package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.SchemaDimensionAttribute;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("schemaDimensionAttributeMapper")
public interface SchemaDimensionAttributeMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SchemaDimensionAttribute record);

    int insertSelective(SchemaDimensionAttribute record);

    SchemaDimensionAttribute selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SchemaDimensionAttribute record);

    int updateByPrimaryKey(SchemaDimensionAttribute record);

    List<SchemaDimensionAttribute> selectAllAttributeByDimensionId(Integer dimensionId);
}