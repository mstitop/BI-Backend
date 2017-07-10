package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.DataxJsonInfo;
import org.springframework.stereotype.Repository;

@Repository("dataxJsonInfoMapper")
public interface DataxJsonInfoMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DataxJsonInfo record);

    int insertSelective(DataxJsonInfo record);

    DataxJsonInfo selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DataxJsonInfo record);

    int updateByPrimaryKey(DataxJsonInfo record);
}