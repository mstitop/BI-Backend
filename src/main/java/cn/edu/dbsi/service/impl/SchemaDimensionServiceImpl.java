package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.SchemaDimensionMapper;
import cn.edu.dbsi.model.SchemaDimension;
import cn.edu.dbsi.service.SchemaDimensionServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("schemaDimensionServiceImpl")
public class SchemaDimensionServiceImpl implements SchemaDimensionServiceI{

    @Autowired
    private SchemaDimensionMapper schemaDimensionMapper;
    public List<SchemaDimension> getAllDimensionBySchemaId(Integer schemaId) {
        return schemaDimensionMapper.selectAllDimensionBySchemaId(schemaId);
    }
}
