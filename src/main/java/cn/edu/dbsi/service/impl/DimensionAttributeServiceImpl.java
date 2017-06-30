package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.SchemaDimensionAttributeMapper;
import cn.edu.dbsi.model.SchemaDimensionAttribute;
import cn.edu.dbsi.service.DimensionAttributeServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 郭世明 on 2017/6/29.
 */
@Service("dimensionAttributeServiceImpl")
public class DimensionAttributeServiceImpl implements DimensionAttributeServiceI {

    @Autowired
    private SchemaDimensionAttributeMapper schemaDimensionAttributeMapper;

    public int addDimensionAttribute(SchemaDimensionAttribute schemaDimensionAttribute) {
        return schemaDimensionAttributeMapper.insert(schemaDimensionAttribute);
    }
}
