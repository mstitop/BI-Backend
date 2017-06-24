package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.DbconnInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("DbconnInfoMapper")
public interface DbconnInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DbconnInfo record);

    int insertSeveral(List<DbconnInfo> record);

    int insertSelective(DbconnInfo record);

    DbconnInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DbconnInfo record);

    int updateByPrimaryKey(DbconnInfo record);

    int updateIsDeleteByPrimaryKey(Integer id);

    List<DbconnInfo> selectAll();
}