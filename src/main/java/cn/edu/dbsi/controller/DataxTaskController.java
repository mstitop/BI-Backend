package cn.edu.dbsi.controller;

import cn.edu.dbsi.dataetl.model.JobInfo;
import cn.edu.dbsi.model.DataxJsonInfo;
import cn.edu.dbsi.model.DataxTask;
import cn.edu.dbsi.model.DbconnInfo;
import cn.edu.dbsi.model.HiveTableInfo;
import cn.edu.dbsi.service.DataxJsonInfoService;
import cn.edu.dbsi.service.DataxTaskService;
import cn.edu.dbsi.service.DbConnectionServiceI;
import cn.edu.dbsi.service.HiveTableInfoService;
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
    /**
     * 产生 datax json 文件，并执行相应导入任务
     *
     * @param token
     * @param json
     * @return
     */
    @RequestMapping(value = "/dataxTask", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> excuteDataxTask(@PathVariable("token") Integer token, @RequestBody Map<String, Object> json,HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<JobInfo> jobList = new ArrayList<JobInfo>();
        DbconnInfo dbconnInfo;

        DataxTask dataxTask = new DataxTask();
        HiveTableInfo hiveTableInfo;

        int tag1 = 0,tag2 = 0, tag3 = 0;
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

        JSONArray tbs = obj.getJSONArray("sourceTableInfos");

        for (int i = 0; i < tbs.length(); i++) {
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

            StringBuffer columns = new StringBuffer();
            for (int k = 0; k < fields.length(); k++) {
                JSONObject field = fields.getJSONObject(k);
                columns.append(field.getString("name"));
                columns.append("-");
                columns.append(field.getString("targetType"));
                columns.append(",");

            }

            // 每个表产生一个任务
            JobInfo jobInfo = new JobInfo();
            jobInfo.setSourceDbType(dbType);
            jobInfo.setSourceDbUsername(dbUsername);
            jobInfo.setSourceDbPassword(dbPassword);
            jobInfo.setSourceTbName(tableName);
            jobInfo.setChannel(jobChannel);
            jobInfo.setColumns(columns.toString());
            jobInfo.setCompress(compressType);
            jobInfo.setWhere(where);
            jobInfo.setFileType(fileType);
            jobInfo.setFileName(targetTbName);
            jobInfo.setPath(jobInfo.getPath() + "/" + targetTbName);

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

            // 根据 targetTbName 和 job 信息，创建 hive 表


            //
        }
        return map;
    }

}
