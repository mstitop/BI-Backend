package cn.edu.dbsi.dataetl.util;

import cn.edu.dbsi.dataetl.model.JobInfo;
import cn.edu.dbsi.model.DataxTask;
import cn.edu.dbsi.service.DataxTaskService;

import java.util.Date;
import java.util.List;

/**
 * Created by Skye on 2017/7/13.
 */
public class DataxExcuteRunnable implements Runnable{


    private DataxTaskService dataxTaskService;

    private DataxTask dataxTask;
    private List<JobInfo> jobList ;
    public DataxExcuteRunnable(DataxTaskService dataxTaskService, DataxTask dataxTask,List<JobInfo> jobList){
        this.dataxTaskService = dataxTaskService;
        this.dataxTask = dataxTask;
        this.jobList = jobList;
    }
    public void run() {
        DataXTaskExcute dataXTaskExcute = new DataXTaskExcute();

        boolean fail_flag = dataXTaskExcute.execute(dataxTaskService,dataxTask, jobList);

        dataxTask.setFinishTime(new Date());

        if (fail_flag) {
            dataxTask.setTaskStatus("0");
        } else {
            dataxTask.setTaskStatus("1");
        }

        try{
            dataxTaskService.updateDataxTask(dataxTask);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
