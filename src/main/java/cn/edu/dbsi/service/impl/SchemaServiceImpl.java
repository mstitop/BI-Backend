package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.SchemaMapper;
import cn.edu.dbsi.model.Schema;
import cn.edu.dbsi.service.SchemaServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 郭世明 on 2017/6/29.
 */
@Service("schemaServiceImpl")
public class SchemaServiceImpl implements SchemaServiceI {

    @Autowired
    private SchemaMapper schemaMapper;

    public int addSchema(Schema schema) {
        return schemaMapper.insert(schema);
    }

    public int getLastSchemaId() {
        return schemaMapper.selectSchemaLastPrimaryKey();
    }

    public int updateSchema(Schema schema) {
        return schemaMapper.updateByPrimaryKeySelective(schema);
    }
}
