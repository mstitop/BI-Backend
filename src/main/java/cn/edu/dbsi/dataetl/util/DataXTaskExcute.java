package cn.edu.dbsi.dataetl.util;

import cn.edu.dbsi.dataetl.model.JobInfo;
import cn.edu.dbsi.model.DataxTask;
import cn.edu.dbsi.service.DataxTaskService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

/**
 * Created by Skye on 2017/6/29.
 */

// 每个任务顺序执行

public class DataXTaskExcute {

    protected static final Log log = LogFactory.getLog(DataXTaskExcute.class);




    public boolean execute(DataxTaskService dataxTaskService,DataxTask dataxTask, List<JobInfo> allTableStatus) {

        boolean breakFlag = false;
        try {
            // 在主方法中初始化后再传入
            /*initAllTableStatus();
            log.info("init All Table Status done" );
            log.info("start job");*/
            int taskId = dataxTask.getId();

            for (int t1 = 0; t1 < allTableStatus.size(); t1++) {
                StringBuffer logInfo = new StringBuffer();
                if (breakFlag) {
                    break;
                }
                JobInfo jobInfo = allTableStatus.get(t1);
                String sourceTableName = jobInfo.getSourceTbName();

                log.info(sourceTableName + " job start to transfer...\n");


                boolean hasException = false;

                try {
                    String cmd = getCommand(taskId, jobInfo);
                    log.info("cmd: " + cmd);
                    hasException = executeCommand(cmd, jobInfo, logInfo);
                    System.out.println(hasException);
                    if (hasException) {
                        breakFlag = true;
                        logInfo.append(dataxTask.getName() + " breaked as got exception in " + jobInfo.getSourceTbName()).append("\n");
                        //log.info(logInfo.toString());

                    }

                } catch (Exception e) {

                    e.printStackTrace();

                    hasException = true;
                    if (hasException) {
                        breakFlag = true;
                        logInfo.append(dataxTask.getName() + " breaked as got exception in " + jobInfo.getSourceTbName()).append("\n");
                        //log.info(logInfo.toString());
                    }
                }

                if (!breakFlag){
                    dataxTask.setProgress((t1 + 1)*100/(allTableStatus.size()*1.0));
                    dataxTaskService.updateDataxTask(dataxTask);
                }
                jobInfo.setHasException(hasException);
                jobInfo.setFinished(true);
                log.info(logInfo.toString());
            }


        } catch (Exception e) {

            e.printStackTrace();
            log.error(e.getMessage(), e);
        } finally {
            log.info("package job done");
        }

        return breakFlag;

    }

    // 执行命令
    private boolean executeCommand(String command, JobInfo jobInfo, StringBuffer logInfo) throws
            IOException {

        String sourceTableName = jobInfo.getSourceTbName();
        boolean hasException = false;

        //Execute
        Process process = Runtime.getRuntime().exec(command);


        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = null;
        line = reader.readLine();

        if (line == null){
            hasException = true;
            jobInfo.setHasException(hasException);
        }
        while (line != null) {
            logInfo.append(sourceTableName + " - " + line).append("\n");
            if (!hasException && line != null && (line.contains("DataXException") || line.contains("SQLException"))) {
                hasException = true;
                jobInfo.setHasException(hasException);
            }
            readExecueOutputLineStatus(line, jobInfo);
            line = reader.readLine();
        }

        logInfo.append(jobInfo.getSourceTbName() + " execute finished!");
        //log.info(logInfo.toString());
        return hasException;
    }

    private String getCommand(int taskId, JobInfo jobInfo) {

        String command = "python " + jobInfo.getDataxFloder() + "/bin/datax.py "
                + jobInfo.getJobFileFloder() + "/" + taskId + "/" + jobInfo.getFileName() + ".json";
        return command;
    }

    private static void readExecueOutputLineStatus(String line, JobInfo jobInfo) {
        if (line != null && line.contains("  :  ")) {
            String[] resultTemp = line.split("  :  ");
            String value = resultTemp[1].trim();
            if (value.matches("\\d+s")) {
                jobInfo.setCostTime(value);
            } else if (value.matches("\\d+[A-Z]*B\\/s")) {
                jobInfo.setReadWriteRateSpeed(value);
            } else if (value.matches("\\d+rec\\/s")) {
                jobInfo.setReadWriteRecordSpeed(value);
            }
        } else if (line != null && line.contains("used") && line.contains("]ms")) {
            int count = Integer.parseInt(line.substring(line.indexOf("used") + 5, line.indexOf("ms") - 1).trim());
            jobInfo.setCostTime(count + "ms");
        } else if (line != null && line.contains("StandAloneJobContainerCommunicator")) {
            int count = Integer.parseInt(line.substring(line.indexOf("Total") + 5, line.indexOf("records")).trim());
            jobInfo.setReadWriteRecords(count);

            int countFail = Integer.parseInt(line.substring(line.indexOf("Error") + 5, line.indexOf("records", line.indexOf("Error"))).trim());
            jobInfo.setReadWriteFailRecords(countFail);

            String speed = line.substring(line.indexOf("Speed") + 5, line.indexOf("B/s") + 3).trim();
            jobInfo.setReadWriteRateSpeed(speed);

            String speedRec = line.substring(line.indexOf(",", line.indexOf("B/s")) + 1, line.indexOf("records/s") + 9).trim();
            jobInfo.setReadWriteRecordSpeed(speedRec);
        }
    }
}
