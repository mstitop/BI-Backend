package cn.edu.dbsi.service;

import cn.edu.dbsi.model.BusinessPackage;
import cn.edu.dbsi.model.BusinessPackageGroup;

import java.util.List;

/**
 * Created by 郭世明 on 2017/6/23.
 */
public interface BusinessPackageServiceI {
    List<BusinessPackageGroup> getBusinessPackageGroup();

    int saveBusinessPackage(BusinessPackage businessPackage);

    int getLastBusinessPackageId();

    int updateBusinessPackage(BusinessPackage businessPackage);

    int deleteBusinessPackage(BusinessPackage businessPackage);

    BusinessPackage getBusinessPackageById(Integer id);

}
