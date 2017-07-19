package cn.edu.dbsi.service;

import cn.edu.dbsi.model.BusinessPackageGroup;

/**
 * Created by 郭世明 on 2017/6/27.
 */
public interface BusinessPackageGroupServiceI {
    int saveBusinessPackageGroup(BusinessPackageGroup businessPackageGroup);

    BusinessPackageGroup getBusinessPackageGroupById(Integer id);

    int upadateBusinessPackageGroupName(BusinessPackageGroup businessPackageGroup);

    int deleteBusinessPackageGroup(Integer id);
}
