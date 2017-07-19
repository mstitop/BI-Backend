package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.BusinessPackageGroupMapper;
import cn.edu.dbsi.model.BusinessPackageGroup;
import cn.edu.dbsi.service.BusinessPackageGroupServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 郭世明 on 2017/6/27.
 */
@Service("businessPackageGroup")
public class BusinessPackageGroupServiceImpl implements BusinessPackageGroupServiceI {

    @Autowired
    private BusinessPackageGroupMapper businessPackageGroupMapper;

    public int saveBusinessPackageGroup(BusinessPackageGroup businessPackageGroup) {
        return businessPackageGroupMapper.insert(businessPackageGroup);
    }

    public BusinessPackageGroup getBusinessPackageGroupById(Integer id) {
        return businessPackageGroupMapper.selectByPrimaryKey(id);
    }

    public int upadateBusinessPackageGroupName(BusinessPackageGroup businessPackageGroup) {
        return businessPackageGroupMapper.updateByPrimaryKeySelective(businessPackageGroup);
    }

    public int deleteBusinessPackageGroup(Integer id) {
        return businessPackageGroupMapper.updateIsDeleteByPrimaryKey(id);
    }
}
