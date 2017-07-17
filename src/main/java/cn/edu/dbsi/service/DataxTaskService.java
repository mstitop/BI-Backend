package cn.edu.dbsi.service;


import cn.edu.dbsi.model.DataxTask;

import java.util.List;

/**
 * Created by Skye on 2017/7/6.
 */
public interface DataxTaskService {

    List<DataxTask> getDataxTasks();

    int saveDataxTask(DataxTask dataxTask);

    int getLastDataxTaskId();

    int updateDataxTask(DataxTask dataxTask);

    int deleteDataxTask(DataxTask dataxTask);

    DataxTask getDataxTaskById(Integer id);
}

