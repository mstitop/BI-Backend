package cn.edu.dbsi.dataetl.util;


import cn.edu.dbsi.dataetl.model.JobInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Skye on 2017/6/27.
 */

public class DataXJobJson {
    protected static final Log log = LogFactory.getLog(DataXJobJson.class);

    public void generateJsonJobFile2(JobInfo config, int taskId){

        JSONObject returnJson = new JSONObject();
        JSONObject jobJson = new JSONObject();
        JSONArray contentArr = new JSONArray();
        JSONObject contentObj = new JSONObject();
        // 构建 reader json
        JSONObject readObj = new JSONObject();
        if (config.getSourceDbType().equalsIgnoreCase("Mysql")){
            readObj.put("name","mysqlreader");
        }else {
            readObj.put("name","oraclereader");
        }
        JSONObject readParaObj = new JSONObject();
        readParaObj.put("column",getSourceDbColumnsList(config.getColumns()));
        JSONArray readerConnArr = new JSONArray();
        JSONObject readerConnObj = new JSONObject();
        List<String> jdbcList  = new ArrayList<String>();
        jdbcList.add(config.getSourceDbUrl());
        readerConnObj.put("jdbcUrl",jdbcList);
        List<String> tableList  = new ArrayList<String>();
        tableList.add(config.getSourceTbName());
        readerConnObj.put("table",tableList);
        readerConnArr.put(readerConnObj);
        readParaObj.put("connection",readerConnArr);
        readParaObj.put("password",config.getSourceDbPassword());
        readParaObj.put("username",config.getSourceDbUsername());
        readParaObj.put("where",config.getWhere());
        readObj.put("parameter",readParaObj);
        contentObj.put("reader",readObj);

        // 构建 writer json
        JSONObject writeObj = new JSONObject();

        writeObj.put("name","hdfswriter");

        JSONObject writeParaObj = new JSONObject();
        writeParaObj.put("column",getHdfsColumnsJsonArr(config.getColumns()));

        writeParaObj.put("compress",config.getCompress());
        writeParaObj.put("defaultFS",config.getDefaultFS());
        writeParaObj.put("fieldDelimiter",config.getFieldDelimiter());
        writeParaObj.put("fileName",config.getFileName());
        writeParaObj.put("fileType",config.getFileType());
        writeParaObj.put("path",config.getPath());
        writeParaObj.put("writeMode",config.getWriteMode());
        writeObj.put("parameter",writeParaObj);
        contentObj.put("writer",writeObj);

        JSONArray  transformerArr = config.getTransformer();
        if (config.getTransformer().length() > 0){
            contentObj.put("transformer",transformerArr);
        }
        contentArr.put(contentObj);

        jobJson.put("content",contentArr);

        JSONObject settingObj = new JSONObject();
        JSONObject speedObj = new JSONObject();
        speedObj.put("channel",config.getChannel());
        settingObj.put("speed",speedObj);

        jobJson.put("setting",settingObj);
        returnJson.put("job",jobJson);

        try {
            log.info("Write job json for table:" + config.getFileName());
            writeToFile(config.getFileName(), returnJson.toString(),config.getJobFileFloder(), taskId);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }


    public void generateJsonJobFile(JobInfo config, int taskId) {

        String json = getTemplate(config.getSourceDbType());

        String cols1 = getSourceDbColumnsString(config.getColumns());
        String cols2 = getHdfsColumnsString(config.getColumns());

        json = json.replace("{job.channel}", String.valueOf(config.getChannel()));

        json = json.replace("{source.db.username}", config.getSourceDbUsername());
        json = json.replace("{source.db.password}", config.getSourceDbPassword());
        json = json.replace("{source.db.table.columns}", cols1);
        json = json.replace("{source.db.table.name}", config.getSourceTbName());
        json = json.replace("{source.db.url}", config.getSourceDbUrl());

        json = json.replace("{source.db.table.where}", config.getWhere());




        json = json.replace("{target.hdfs.compress}", config.getCompress());
        json = json.replace("{target.hdfs.defaultFS}", config.getDefaultFS());
        json = json.replace("{target.hdfs.fieldDelimiter}", config.getFieldDelimiter());
        json = json.replace("{target.hdfs.fileName}", config.getFileName());
        json = json.replace("{target.hdfs.fileType}", config.getFileType());
        json = json.replace("{target.hdfs.path}", config.getPath());
        json = json.replace("{target.hdfs.writeMode}", config.getWriteMode());
        json = json.replace("{target.hdfs.columns}", cols2);



        log.info(json);

        try {
            log.info("Write job json for table:"+config.getFileName());
            writeToFile(config.getFileName(), json,config.getJobFileFloder(), taskId);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
    private void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = DataXJobJson.class.getClassLoader().getResourceAsStream(filePath);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        String line;
        line = reader.readLine();
        while (line != null) {
            buffer.append(line);
            buffer.append("\n");
            line = reader.readLine();
        }
        reader.close();
    }
    private void writeToFile(String fileName, String json,String folder, int taskId) throws IOException {
        String dirStr = folder + "/" + taskId;
        String fileStr = dirStr + "/" + fileName + ".json";
        File dirFile = new File(dirStr);

        dirFile.mkdirs();

        File file = new File(fileStr);
        //OutputStreamWriter write = new OutputStreamWriter(new FileOutputStream(file),"UTF-8");
        //BufferedWriter out = new BufferedWriter(new FileWriter(file),"UTF-8");
        BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
                new FileOutputStream(file), "UTF-8"));
        //BufferedWriter out = new BufferedWriter(write);
        out.write(json);
        out.close();
        log.info("Write json to file:"+file.getAbsolutePath());
    }
    public String getTemplate(String templateName) {

        StringBuffer stb = new StringBuffer();
            try {
                if (templateName.equals("Mysql")){
                    readToBuffer(stb, "etljob/msql2hdfsTemplete.json");
                }else {
                    readToBuffer(stb, "etljob/oracle2hdfsTemplete.json");
                }

            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }


        return stb.toString();
    }

    private String getSourceDbColumnsString(Map<String,String> columns) {
        StringBuffer stb = new StringBuffer();
        int i = 0;
        int count = columns.size();
        for (Map.Entry<String,String> entry : columns.entrySet()) {
            i++;
            stb.append("\"");
            stb.append(entry.getKey());
            stb.append("\"");
            if(i < count){
                stb.append(",");
            }
        }
        return stb.toString();
    }
    private List<String> getSourceDbColumnsList(Map<String,String> columns) {
        StringBuffer stb = new StringBuffer();
        List<String> list = new ArrayList<String>();
        for (Map.Entry<String,String> entry : columns.entrySet()) {
            list.add(entry.getKey());
        }
        return list;
    }
    private String getHdfsColumnsString(Map<String,String> columns) {
        StringBuffer stb = new StringBuffer();
        int i = 0;
        int count = columns.size();
        for (Map.Entry<String,String> entry : columns.entrySet()) {

            i++;
            stb.append("{");
            stb.append("\"name\": ");
            stb.append("\"");
            stb.append(entry.getKey());
            stb.append("\",");
            stb.append("\"type\": ");
            stb.append("\"");
            stb.append(entry.getValue());
            stb.append("\"");
            stb.append("}");
            if(i < count){
                stb.append(",");
            }
        }
        return stb.toString();
    }
    private JSONArray getHdfsColumnsJsonArr(Map<String,String> columns) {
       JSONArray returnArr = new JSONArray();
        for (Map.Entry<String,String> entry : columns.entrySet()) {
            JSONObject colObj = new JSONObject();
            colObj.put("name",entry.getKey());
            colObj.put("type",entry.getValue());
            returnArr.put(colObj);
        }
        return returnArr;
    }
}
