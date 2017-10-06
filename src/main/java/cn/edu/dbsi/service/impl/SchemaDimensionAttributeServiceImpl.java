package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.SchemaDimensionAttributeMapper;
import cn.edu.dbsi.model.SchemaDimensionAttribute;
import cn.edu.dbsi.service.SchemaDimensionAttributeServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
@Service("schemaDimensionAttributeServiceImpl")
public class SchemaDimensionAttributeServiceImpl implements SchemaDimensionAttributeServiceI{
    @Autowired
    private SchemaDimensionAttributeMapper schemaDimensionAttributeMapper;

    public List<SchemaDimensionAttribute> getAllAttributeByDimensionId(Integer dimensionId) {
        return schemaDimensionAttributeMapper.selectAllAttributeByDimensionId(dimensionId);
    }
}
