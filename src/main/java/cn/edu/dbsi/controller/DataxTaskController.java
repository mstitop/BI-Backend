package cn.edu.dbsi.controller;

import cn.edu.dbsi.dataetl.model.JobInfo;
import cn.edu.dbsi.dataetl.util.DataXJobJson;
import cn.edu.dbsi.dataetl.util.DataxExcuteRunnable;
import cn.edu.dbsi.dataetl.util.JobConfig;
import cn.edu.dbsi.interceptor.LoginRequired;
import cn.edu.dbsi.model.*;
import cn.edu.dbsi.service.*;
import cn.edu.dbsi.util.DBUtils;
import cn.edu.dbsi.util.StatusUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * Created by Skye on 2017/7/6.
 */
@LoginRequired
@Controller
@RequestMapping(value = "/rest")
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

    @Autowired
    private JobConfig jobConfig;


    /**
     * 产生 datax json 文件，并执行相应导入任务
     *
     * @param json
     * @return
     */
    @RequestMapping(value = "/datax-task", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> excuteDataxTask( @RequestBody Map<String, Object> json, HttpServletRequest request) {
//        GenericXmlApplicationContext context = new GenericXmlApplicationContext();
//        context.setValidating(false);
//        context.load("classpath*:spring.xml");
//        context.refresh();

        Map<String, Object> map = new HashMap<String, Object>();
        List<JobInfo> jobList = new ArrayList<JobInfo>();
        DbconnInfo dbconnInfo;
        JobInfo jobInfo;
        DataxTask dataxTask = new DataxTask();

        int tag1, tag2 = 0, tag3 = 0, tag4 = 0, tag5 = 0;
        JSONObject obj = new JSONObject(json);
        String taskName = obj.getString("name");
        int businessPackageId = obj.getInt("businessPackageId");

        dataxTask.setName(taskName);
        dataxTask.setBusinessPackageId(businessPackageId);
        dataxTask.setIsDelete("0");
        dataxTask.setCreateTime(new Date());
        dataxTask.setTaskStatus("0");

        tag1 = dataxTaskService.saveDataxTask(dataxTask);
        int lastTaskId = dataxTaskService.getLastDataxTaskId();
        dataxTask.setId(lastTaskId);

        String targetHiveDbName = "bi_" + lastTaskId;
        dataxTask.setHiveAddress(jobConfig.getPath() + "/" + targetHiveDbName+ ".db/");


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
                dbName = dbUsername;
            }else {
                dbName = dbUrl.substring(dbUrl.lastIndexOf("/") + 1,dbUrl.length());
            }

            // 获取表配置信息
            String tableName = table.getString("tableName");
            int jobChannel = table.getInt("jobChannel");

            String fileType = table.getString("fileType");
            if (fileType.equals("无")){
                fileType = "text";
            }
            String compressType = table.getString("zipType");
            if (compressType.equals("无")){
                compressType = "";
            }
            String where = table.getString("where");
            if (where.equals("无")){
                where = "";
            }

            String targetTbName = tableName + "__" + dbName;

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

            jobInfo.setDefaultFS(jobConfig.getDefaultFS());
            String tablePath = jobConfig.getPath() + "/" +targetHiveDbName + ".db/" + targetTbName;
            jobInfo.setPath(tablePath.toLowerCase());
            jobInfo.setFieldDelimiter(jobConfig.getFieldDelimiter());
            jobInfo.setWriteMode(jobConfig.getWriteMode());
            jobInfo.setDataxFloder(jobConfig.getDataxFloder());
            jobInfo.setJobFileFloder(request.getSession().getServletContext().getRealPath("/datax"));

            jobList.add(jobInfo);

            // 赋值并存储任务表
            DataxJsonInfo dataxJsonInfo = new DataxJsonInfo();
            dataxJsonInfo.setTaskId(lastTaskId);
            dataxJsonInfo.setName(targetTbName + ".json");
            dataxJsonInfo.setTableName(targetTbName);
            dataxJsonInfo.setIsDelete("0");
            dataxJsonInfo.setDbId(dbId);
            String jsonAddress = request.getSession().getServletContext().getRealPath("/datax") + File.separator;
            dataxJsonInfo.setJsonAddress(jsonAddress +"/"+ lastTaskId +"/"+ targetTbName + ".json");
            tag2 = dataxJsonInfoService.addJsonInfo(dataxJsonInfo);


            // 根据 targetTbName 和 job 信息，创建 hive 表，并存储 hive 信息表
            HiveTableInfo hiveTableInfo = new HiveTableInfo();
            hiveTableInfo.setTableName(targetTbName);
            hiveTableInfo.setTaskId(lastTaskId);

            tag3 = hiveTableInfoService.addHiveTableInfo(hiveTableInfo);
            if(i == 0){
                tag4 = DBUtils.createHiveDb(jobConfig, targetHiveDbName);
            }
            tag5 = DBUtils.createHiveTable(jobConfig,  targetHiveDbName + "." + targetTbName, columnsMap);

            // 生成任务 json 文件
            DataXJobJson dataXJobJson = new DataXJobJson();
            dataXJobJson.generateJsonJobFile(jobInfo, lastTaskId);
        }

        // 开启任务执行线程
        Runnable excuteRunnable = new DataxExcuteRunnable(dataxTaskService,dataxTask,jobList);
        Thread thread = new Thread(excuteRunnable);
        thread.start();

        Map<String, Object> errorMap = new HashMap<String, Object>();

        if(tag2 == 0){
            return    StatusUtil.error("","存储 datax json 信息表出错");
        }else if(tag4 == 0){
            return    StatusUtil.error("","创建 hive 数据库出错");
        }else if(tag5 == 0){
            return    StatusUtil.error("","创建 hive 数据表出错");
        }else if (tag1 == 1 && tag2 == 1 && tag3 == 1 && tag4 == 1 && tag5 == 1) {
            return   StatusUtil.updateOk();
        } else {

            return    StatusUtil.error("","创建导入任务失败");
        }

    }

    /**
     *  查询所有导入任务信息，并返回
     * @return
     */
    @RequestMapping(value = "/datax-tasks", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getDataxTasksInfo(@RequestParam Integer page,@RequestParam Integer size) {

        int count = dataxTaskService.countTask();
        int start = (page-1)*size;
        int pageNum = count/size + 1;
        Map<String,Object> returnMap = new HashMap<String,Object>();

        Map<String,Object> pagMap = new HashMap<String,Object>();
        pagMap.put("start",start);
        pagMap.put("pagesize",size);

        List<DataxTask> dataxTasks = dataxTaskService.getDataxTasksByPage(pagMap);

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
            Map<String,Object> temp = new HashMap<String,Object>();
            temp.put("total",count);
            temp.put("pages",pageNum);
            returnMap.put("pagination", temp);
            returnMap.put("tasks", tasks);
            return StatusUtil.querySuccess(returnMap);
        }else {
            return StatusUtil.error("","查询失败");
        }

    }

    /**
     *  查询单条导入任务信息，并返回
     * @return
     */
    @RequestMapping(value = "/datax-task/{taskId}/status", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getDataxTaskInfoById(@PathVariable("taskId") Integer taskid) {
        Map<String, Object> map = new HashMap<String, Object>();
        DataxTask dataxTask = dataxTaskService.getDataxTaskById(taskid);

        Map<String, Object> errorMap = new HashMap<String, Object>();

        if(dataxTask != null ){
            map.put("taskStatus", Integer.parseInt(dataxTask.getTaskStatus()));
            return StatusUtil.querySuccess(map);
        }else {
            return  StatusUtil.error("","查询失败");

        }



    }

}
