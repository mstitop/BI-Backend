package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.BusinessPackageGroupMapper;
import cn.edu.dbsi.model.BusinessPackageGroup;
import cn.edu.dbsi.service.BusinessPackageServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 郭世明 on 2017/6/23.
 */
@Service("businessPackageService")
public class BusinessPackageServiceImpl implements BusinessPackageServiceI {

    @Autowired
    private BusinessPackageGroupMapper businessPackageGroupMapper;

    public List<BusinessPackageGroup> getBusinessPackageGroup() {
        List<Integer> ids = businessPackageGroupMapper.selectPrimaryKeys();
        return businessPackageGroupMapper.selectAllBusinessGroup(ids);
    }
}
