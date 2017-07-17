package cn.edu.dbsi.controller;

import cn.edu.dbsi.dataetl.model.HiveConnInfo;
import cn.edu.dbsi.dataetl.model.JobInfo;
import cn.edu.dbsi.dataetl.util.DataXJobJson;
import cn.edu.dbsi.dataetl.util.DataxExcuteRunnable;
import cn.edu.dbsi.model.*;
import cn.edu.dbsi.service.*;
import cn.edu.dbsi.util.DBUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * Created by Skye on 2017/7/6.
 */
public class DataxTaskController {

    @Autowired
    private DataxTaskService dataxTaskService;
    @Autowired
    private DbConnectionServiceI dbConnectionServiceI;
    @Autowired
    private DataxJsonInfoService dataxJsonInfoService;
    @Autowired
    private HiveTableInfoService hiveTableInfoService;
    @Autowired
    private BusinessPackageServiceI businessPackageServiceI;

    /**
     * 产生 datax json 文件，并执行相应导入任务
     *
     * @param token
     * @param json
     * @return
     */
    @RequestMapping(value = "/datax-task", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> excuteDataxTask(@PathVariable("token") Integer token, @RequestBody Map<String, Object> json,HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<JobInfo> jobList = new ArrayList<JobInfo>();
        DbconnInfo dbconnInfo;
        JobInfo jobInfo = new JobInfo();
        DataxTask dataxTask = new DataxTask();

        int tag1, tag2 = 0, tag3 = 0, tag4 = 0, tag5 = 0;
        JSONObject obj = new JSONObject(json);
        String taskName = obj.getString("name");
        int businessPackageId = Integer.parseInt(obj.getString("businessPackageId"));

        dataxTask.setName(taskName);
        dataxTask.setBusinessPackageId(businessPackageId);
        dataxTask.setIsDelete("0");
        dataxTask.setCreateTime(new Date());
        dataxTask.setTaskStatus("0");

        tag1 = dataxTaskService.saveDataxTask(dataxTask);
        int lastTaskId = dataxTaskService.getLastDataxTaskId();


        dataxTask.setHiveAddress(jobInfo.getPath() + "/" + lastTaskId + ".db/");


        JSONArray tbs = obj.getJSONArray("sourceTableInfos");

        for (int i = 0; i < tbs.length(); i++) {
            Map<String, String> columnsMap = new LinkedHashMap<String, String>();
            JSONObject table = tbs.getJSONObject(i);
            int dbId = table.getInt("dbid");
            // 获取数据库信息
            dbconnInfo = dbConnectionServiceI.getDbconnInfoById(dbId);
            String dbUrl = dbconnInfo.getUrl();
            String dbType = dbconnInfo.getCategory();
            String dbUsername = dbconnInfo.getUsername();
            String dbPassword = dbconnInfo.getPassword();
            String dbName = "";
            if(dbType.equals("Oracle")){
                dbName = dbType;
            }else {
                dbName = dbType.substring(dbType.lastIndexOf("/"),dbType.length());
            }

            // 获取表配置信息
            String tableName = table.getString("tableName");
            int jobChannel = table.getInt("jobChannel");
            String fileType = table.getString("fileType");
            String compressType = table.getString("zipType");
            String where = table.getString("where");
            String targetTbName = tableName + "_" + dbName;

            JSONArray fields = table.getJSONArray("fields");

            //StringBuffer columns = new StringBuffer();
            for (int k = 0; k < fields.length(); k++) {
                JSONObject field = fields.getJSONObject(k);
//                columns.append(field.getString("name"));
//                columns.append("-");
//                columns.append(field.getString("targetType"));
//                columns.append(",");
                columnsMap.put(field.getString("name"), field.getString("targetType"));

            }

            // 每个表产生一个任务
            jobInfo = new JobInfo();
            jobInfo.setSourceDbType(dbType);
            jobInfo.setSourceDbUrl(dbUrl);
            jobInfo.setSourceDbUsername(dbUsername);
            jobInfo.setSourceDbPassword(dbPassword);
            jobInfo.setSourceTbName(tableName);
            jobInfo.setChannel(jobChannel);
            jobInfo.setColumns(columnsMap);
            jobInfo.setCompress(compressType);
            jobInfo.setWhere(where);
            jobInfo.setFileType(fileType);
            jobInfo.setFileName(targetTbName);
            jobInfo.setPath(jobInfo.getPath() + "/" + lastTaskId + ".db/" + targetTbName);
            jobList.add(jobInfo);

            // 赋值并存储任务表
            DataxJsonInfo dataxJsonInfo = new DataxJsonInfo();
            dataxJsonInfo.setTaskId(lastTaskId);
            dataxJsonInfo.setName(targetTbName + ".json");
            dataxJsonInfo.setTableName(targetTbName);
            dataxJsonInfo.setIsDelete("0");
            dataxJsonInfo.setDbId(dbId);
            String jsonAddress = request.getSession().getServletContext().getRealPath("/datax") + File.separator;
            dataxJsonInfo.setJsonAddress(jsonAddress + targetTbName);

            tag2 = dataxJsonInfoService.addJsonInfo(dataxJsonInfo);

            // 根据 targetTbName 和 job 信息，创建 hive 表，并存储 hive 信息表
            HiveTableInfo hiveTableInfo = new HiveTableInfo();
            hiveTableInfo.setTableName(targetTbName);
            hiveTableInfo.setTaskId(lastTaskId);

            tag3 = hiveTableInfoService.addHiveTableInfo(hiveTableInfo);

            HiveConnInfo hiveConnInfo = new HiveConnInfo();
            tag4 = DBUtils.createHiveTable(hiveConnInfo, lastTaskId + "." + targetTbName, columnsMap);

            // 生成任务 json 文件
            DataXJobJson dataXJobJson = new DataXJobJson();
            dataXJobJson.generateJsonJobFile(jobInfo, lastTaskId);
        }

        // 开启任务执行线程
        Runnable excuteRunnable = new DataxExcuteRunnable(dataxTask,jobList);
        Thread thread = new Thread(excuteRunnable);
        thread.start();

        Map<String, Object> errorMap = new HashMap<String, Object>();

        if (tag1 == 1 && tag2 == 1 && tag3 == 1 && tag4 == 1) {
            map.put("success",true);
        } else {

            errorMap.put("code",40001);
            errorMap.put("message","Unauthorized");
            map.put("status", 456);
            map.put("error", errorMap);
        }
        return map;
    }

    /**
     *  查询所有导入任务信息，并返回
     * @param token
     * @return
     */
    @RequestMapping(value = "/datax-tasks", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getDataxTasksInfo(@PathVariable("token") Integer token) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<DataxTask> dataxTasks = dataxTaskService.getDataxTasks();

        List<Map<String, Object>> tasks = new ArrayList<Map<String, Object>>();
        BusinessPackage businessPackage;
        if (dataxTasks != null){
            for(DataxTask dataxTask : dataxTasks){
                Map<String, Object> temp = new HashMap<String, Object>();
                temp.put("id",dataxTask.getId());
                temp.put("name",dataxTask.getName());
                businessPackage = businessPackageServiceI.getBusinessPackageById(dataxTask.getBusinessPackageId());
                temp.put("bpName",businessPackage.getName());
                temp.put("createTime",dataxTask.getCreateTime());
                if (dataxTask.getFinishTime() != null && !"".equals(dataxTask.getFinishTime())) {
                    temp.put("finishTime",dataxTask.getFinishTime());
                }else {
                    temp.put("finishTime","");
                }

                temp.put("taskStatus",dataxTask.getTaskStatus());
                tasks.add(temp);
            }

            map.put("result", 1);
            map.put("tasks", tasks);
        }else {
            map.put("result", 0);
            map.put("error", "获取失败");
        }
        return  map;
    }

    /**
     *  查询单条导入任务信息，并返回
     * @param token
     * @return
     */
    @RequestMapping(value = "/datax-task/{taskId}/status", method = RequestMethod.GET)
    @ResponseBody
    public Map<String, Object> getDataxTaskInfoById(@PathVariable("token") Integer token,@PathVariable("taskId") Integer taskid) {
        Map<String, Object> map = new HashMap<String, Object>();
        DataxTask dataxTask = dataxTaskService.getDataxTaskById(taskid);
        if(dataxTask != null ){
            map.put("result", 1);
            map.put("status", Integer.parseInt(dataxTask.getTaskStatus()));
        }else {
            map.put("result", 0);
            map.put("error", "获取失败");
        }


        return  map;
    }
}
