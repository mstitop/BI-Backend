package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.DbBusinessPackage;
import org.springframework.stereotype.Repository;

@Repository("DbBusinessPackageMapper")
public interface DbBusinessPackageMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DbBusinessPackage record);

    int insertSelective(DbBusinessPackage record);

    DbBusinessPackage selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DbBusinessPackage record);

    int updateByPrimaryKey(DbBusinessPackage record);
}