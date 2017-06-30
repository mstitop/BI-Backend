package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.SchemaMeasureMapper;
import cn.edu.dbsi.model.SchemaMeasure;
import cn.edu.dbsi.service.MeasuresServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 郭世明 on 2017/6/29.
 */
@Service("measuresServiceImpl")
public class MeasuresServiceImpl implements MeasuresServiceI {

    @Autowired
    private SchemaMeasureMapper schemaMeasureMapper;

    public int addMeasures(SchemaMeasure schemaMeasure) {
        return schemaMeasureMapper.insert(schemaMeasure);
    }
}
