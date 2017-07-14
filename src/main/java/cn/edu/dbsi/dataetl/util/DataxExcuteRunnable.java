package cn.edu.dbsi.dataetl.util;

import cn.edu.dbsi.dataetl.model.JobInfo;
import cn.edu.dbsi.model.DataxTask;
import cn.edu.dbsi.service.DataxTaskService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * Created by Skye on 2017/7/13.
 */
public class DataxExcuteRunnable implements Runnable{

    @Autowired
    private DataxTaskService dataxTaskService;

    private DataxTask dataxTask;
    private List<JobInfo> jobList ;
    public DataxExcuteRunnable(DataxTask dataxTask,List<JobInfo> jobList){
        this.dataxTask = dataxTask;
        this.jobList = jobList;
    }
    public void run() {
        DataXTaskExcute dataXTaskExcute = new DataXTaskExcute();

        boolean fail_flag = dataXTaskExcute.execute(dataxTask, jobList);

        dataxTask.setFinishTime(new Date());

        if (fail_flag) {
            dataxTask.setTaskStatus("2");

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
