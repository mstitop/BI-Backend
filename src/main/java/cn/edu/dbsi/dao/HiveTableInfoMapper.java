package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.HiveTableInfo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("hiveTableInfoMapper")
public interface HiveTableInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(HiveTableInfo record);

    int insertSelective(HiveTableInfo record);

    HiveTableInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(HiveTableInfo record);

    int updateByPrimaryKey(HiveTableInfo record);

    List<String> selectTableNameBytask(int taskId);
}