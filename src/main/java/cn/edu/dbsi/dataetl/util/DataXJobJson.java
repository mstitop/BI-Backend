package cn.edu.dbsi.dataetl.util;


import cn.edu.dbsi.dataetl.model.JobInfo;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.Map;

/**
 * Created by Skye on 2017/6/27.
 */

public class DataXJobJson {
    protected static final Log log = LogFactory.getLog(DataXJobJson.class);
    private static String template = null;



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



        //log.info(json);

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

        BufferedWriter out = new BufferedWriter(new FileWriter(file));
        out.write(json);
        out.close();
        log.info("Write json to file:"+file.getAbsolutePath());
    }
    private String getTemplate(String templateName) {
        if (template == null) {
            StringBuffer stb = new StringBuffer();
            try {
                if (templateName.equalsIgnoreCase("mysql")){
                    readToBuffer(stb, "job/msql2hdfsTemplete.json");
                }else {
                    readToBuffer(stb, "job/oracle2hdfsTemplete.json");
                }

            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            template = stb.toString();
            log.info(template);
        }

        return template;
    }

    private String getSourceDbColumnsString(Map<String,String> columns) {
        StringBuffer stb = new StringBuffer();
        int i = 0;
        int count = columns.size();
        for (Map.Entry<String,String> entry : columns.entrySet()) {
            stb.append("\"");
            stb.append(entry.getKey());
            stb.append("\"");
            if(i < count - 1){
                stb.append(",");
            }
        }
        return stb.toString();
    }
    private String getHdfsColumnsString(Map<String,String> columns) {
        StringBuffer stb = new StringBuffer();
        int i = 0;
        int count = columns.size();
        for (Map.Entry<String,String> entry : columns.entrySet()) {


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
            if(i < count - 1){
                stb.append(",");
            }
        }
        return stb.toString();
    }
}
