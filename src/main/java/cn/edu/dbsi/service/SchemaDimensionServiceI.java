package cn.edu.dbsi.service;

import cn.edu.dbsi.model.SchemaDimension;

import java.util.List;

public interface SchemaDimensionServiceI {
    List<SchemaDimension> getAllDimensionBySchemaId(Integer schemaId);
}
