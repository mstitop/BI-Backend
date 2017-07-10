package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.DataxTask;

public interface DataxTaskMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DataxTask record);

    int insertSelective(DataxTask record);

    DataxTask selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DataxTask record);

    int updateByPrimaryKey(DataxTask record);
}