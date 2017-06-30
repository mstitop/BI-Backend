package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.SchemaDimensionAttributeMapper;
import cn.edu.dbsi.dao.SchemaDimensionMeasureMapper;
import cn.edu.dbsi.model.SchemaDimensionMeasure;
import cn.edu.dbsi.service.DimensionLinkServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 郭世明 on 2017/6/29.
 */
@Service("dimensionLinkServiceImpl")
public class DimensionLinkServiceImpl implements DimensionLinkServiceI {

    @Autowired
    private SchemaDimensionMeasureMapper schemaDimensionMeasureMapper;

    public int addDimensionLink(SchemaDimensionMeasure schemaDimensionMeasure) {
        return schemaDimensionMeasureMapper.insert(schemaDimensionMeasure);
    }
}
