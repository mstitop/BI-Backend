package cn.edu.dbsi.service;

import cn.edu.dbsi.model.SchemaDimension;

/**
 * Created by 郭世明 on 2017/6/29.
 */
public interface DimensionServiceI {
    int addDimension(SchemaDimension schemaDimension);

    int getLastDimensionId();
}
