package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.BusinessPackage;
import org.springframework.stereotype.Repository;

@Repository("BusinessPackageMapper")
public interface BusinessPackageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BusinessPackage record);

    int insertSelective(BusinessPackage record);

    BusinessPackage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(BusinessPackage record);

    int updateByPrimaryKey(BusinessPackage record);

    int selectLastPrimaryKey();

    int updateIsDeleteByPrimaryKey(BusinessPackage businessPackage);
}