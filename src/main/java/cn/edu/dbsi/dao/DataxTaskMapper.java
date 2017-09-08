package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.DataxTask;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository("dataxTaskMapper")
public interface DataxTaskMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(DataxTask record);

    int insertSelective(DataxTask record);

    DataxTask selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(DataxTask record);

    int updateByPrimaryKey(DataxTask record);

    List<DataxTask> selectAll();

    List<DataxTask> selectPage(Map<String, Object> map);

    int updateIsDeleteByPrimaryKey(DataxTask record);

    int countTask();

    int selectLastPrimaryKey();
}