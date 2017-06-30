package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.SchemaDimensionMapper;
import cn.edu.dbsi.model.SchemaDimension;
import cn.edu.dbsi.service.DimensionServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 郭世明 on 2017/6/29.
 */
@Service("dimensionServiceImpl")
public class DimensionServiceImpl implements DimensionServiceI {

    @Autowired
    private SchemaDimensionMapper schemaDimensionMapper;

    public int addDimension(SchemaDimension schemaDimension) {
        return schemaDimensionMapper.insert(schemaDimension);
    }

    public int getLastDimensionId() {
        return schemaDimensionMapper.selectSchemaDimensionLastPrimaryKey();
    }
}
