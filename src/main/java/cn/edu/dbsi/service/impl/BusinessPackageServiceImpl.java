package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.BusinessPackageGroupMapper;
import cn.edu.dbsi.dao.BusinessPackageMapper;
import cn.edu.dbsi.model.BusinessPackage;
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

    @Autowired
    private BusinessPackageMapper businessPackageMapper;

    public List<BusinessPackageGroup> getBusinessPackageGroup() {
        List<Integer> list = businessPackageGroupMapper.selectPrimaryKeys();
        return businessPackageGroupMapper.selectAllBusinessGroup(list);
    }

    public int saveBusinessPackage(BusinessPackage businessPackage) {
        return businessPackageMapper.insert(businessPackage);
    }

    public int getLastBusinessPackageId() {
        return businessPackageMapper.selectLastPrimaryKey();
    }

    public int updateBusinessPackage(BusinessPackage businessPackage) {
        return businessPackageMapper.updateByPrimaryKeySelective(businessPackage);
    }

    public int deleteBusinessPackage(BusinessPackage businessPackage) {
        return businessPackageMapper.updateIsDeleteByPrimaryKey(businessPackage);
    }

    public BusinessPackage getBusinessPackageById(Integer id) {
        return businessPackageMapper.selectByPrimaryKey(id);
    }
}
