package cn.edu.dbsi.service;

import cn.edu.dbsi.model.DataxTask;

import java.util.List;
import java.util.Map;

/**
 * Created by Skye on 2017/7/6.
 */
public interface DataxTaskService {

    List<DataxTask> getDataxTasks();

    List<DataxTask> getDataxTasksByPage(Map<String,Object> map);
    int saveDataxTask(DataxTask dataxTask);

    int getLastDataxTaskId();
    int countTask();
    int updateDataxTask(DataxTask dataxTask);

    int deleteDataxTask(DataxTask dataxTask);

    DataxTask getDataxTaskById(Integer id);
}

