package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.DataxTaskMapper;
import cn.edu.dbsi.model.DataxTask;
import cn.edu.dbsi.service.DataxTaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Skye on 2017/7/6.
 */

@Service("dataxTaskService")
public class DataxTaskServiceImp implements DataxTaskService{
    @Autowired
    private DataxTaskMapper dataxTaskMapper;

    public List<DataxTask> getDataxTasks() {
        return dataxTaskMapper.selectAll();
    }

    public int saveDataxTask(DataxTask dataxTask) {
        return dataxTaskMapper.insert(dataxTask);
    }

    public int getLastDataxTaskId() {
        return dataxTaskMapper.selectLastPrimaryKey();
    }

    public int updateDataxTask(DataxTask dataxTask) {
        return dataxTaskMapper.updateByPrimaryKeySelective(dataxTask);
    }

    public int deleteDataxTask(DataxTask dataxTask) {
        return dataxTaskMapper.updateIsDeleteByPrimaryKey(dataxTask);
    }

    public DataxTask getDataxTaskById(Integer id) {
        return dataxTaskMapper.selectByPrimaryKey(id);
    }
}
