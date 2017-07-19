package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.BusinessPackageGroup;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("BusinessPackageGroupMapper")
public interface BusinessPackageGroupMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(BusinessPackageGroup record);

    int insertSelective(BusinessPackageGroup record);

    List<BusinessPackageGroup> selectAllBusinessGroup(List<Integer> list);

    int updateByPrimaryKeySelective(BusinessPackageGroup record);

    int updateByPrimaryKey(BusinessPackageGroup record);

    List<Integer> selectPrimaryKeys();

    BusinessPackageGroup selectByPrimaryKey(Integer id);

    int updateIsDeleteByPrimaryKey(Integer id);
}