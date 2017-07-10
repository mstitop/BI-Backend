package cn.edu.dbsi.service;

import cn.edu.dbsi.model.Schema;

/**
 * Created by 郭世明 on 2017/6/29.
 */
public interface SchemaServiceI {
    int addSchema(Schema schema);

    int getLastSchemaId();

    int updateSchema(Schema schema);
}
