package com.yxt.data.migration.json.service;

import com.yxt.data.migration.json.model.JobInfo;
import com.yxt.data.migration.util.DataXJobFile;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Service;

import java.io.*;

/**
 * Created by Skye on 2017/6/27.
 */
@Service
public class DataXJobJson {
    protected static final Log log = LogFactory.getLog(DataXJobJson.class);
    private static String template = null;



    public void generateJsonJobFile(JobInfo config, int packageId) {

        String json = getTemplate(config.getSourceDbType());

        CharSequence cols1 = getSourceDbColumnsString(config.getColumns());
        CharSequence cols2 = getHdfsColumnsString(config.getColumns());

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
            log.info("Write job json for table:"+config.getSourceTbName());
            writeToFile(config.getSourceTbName(), json,config.getJobFileFloder(),packageId);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
    private void readToBuffer(StringBuffer buffer, String filePath) throws IOException {
        InputStream is = DataXJobFile.class.getClassLoader().getResourceAsStream(filePath);
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
    private void writeToFile(String fileName, String json,String folder, int packageId) throws IOException {
        String dirStr = folder + "/" + packageId;
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

    private CharSequence getSourceDbColumnsString(String columns) {
        StringBuffer stb = new StringBuffer();
        String[] strs = columns.split(",");
        for (String s : strs) {
            stb.append("\"");
            stb.append(s.split("-")[0]);
            stb.append("\",");
        }
        return stb.subSequence(0, stb.length() - 1);
    }
    private CharSequence getHdfsColumnsString(String columns) {
        StringBuffer stb = new StringBuffer();
        String[] strs = columns.split(",");
        for (String s : strs) {

            String[] colAndType = s.split("-");
            stb.append("{");
            stb.append("\"name\": ");
            stb.append("\"");
            stb.append(colAndType[0]);
            stb.append("\",");
            stb.append("\"type\": ");
            stb.append("\"");
            stb.append(colAndType[1]);
            stb.append("\"");
            stb.append("},");
        }
        return stb.subSequence(0, stb.length() - 1);
    }
}
