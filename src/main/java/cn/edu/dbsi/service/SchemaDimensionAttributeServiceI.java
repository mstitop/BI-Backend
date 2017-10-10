package cn.edu.dbsi.service;

import cn.edu.dbsi.model.SchemaDimensionAttribute;

import java.util.List;

public interface SchemaDimensionAttributeServiceI {
    List<SchemaDimensionAttribute> getAllAttributeByDimensionId(Integer dimensionId);
}
