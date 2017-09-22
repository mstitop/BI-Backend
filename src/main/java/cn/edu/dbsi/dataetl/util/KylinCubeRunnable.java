package cn.edu.dbsi.dataetl.util;

import cn.edu.dbsi.dto.CubeSchema;
import cn.edu.dbsi.model.CubeInfo;
import cn.edu.dbsi.service.CubeInfoServiceI;
import cn.edu.dbsi.service.SchemaServiceI;
import cn.edu.dbsi.util.HttpConnectDeal;
import cn.edu.dbsi.util.SchemaUtils;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.Date;

/**
 * Created by Skye on 2017/9/4.
 */
public class KylinCubeRunnable implements Runnable{
    private CubeSchema cubeSchema;
    private SchemaServiceI schemaServiceI;
    private CubeInfoServiceI cubeInfoServiceI;
    private CubeInfo cubeInfo;
    private JobConfig jobConfig;
    private String path;
    private String uuid;
    public KylinCubeRunnable(String uuid,CubeInfo cubeInfo,CubeSchema cubeSchema, SchemaServiceI schemaServiceI, CubeInfoServiceI cubeInfoServiceI, JobConfig jobConfig, String path) {
        this.uuid = uuid;
        this.cubeSchema = cubeSchema;
        this.schemaServiceI = schemaServiceI;
        this.jobConfig = jobConfig;
        this.path = path;
        this.cubeInfoServiceI = cubeInfoServiceI;
        this.cubeInfo = cubeInfo;
    }

    public void run() {
        final long timeInterval = 120000;// 两分钟运行一次
        boolean tag = true;
        while (tag) {
            // ------- code for task to run
            String kylinGetCubeApi = jobConfig.getKylinUrl() + "/api/cubes/" + cubeSchema.getCubeName();
            String kylinGetJobApi = jobConfig.getKylinUrl() + "/api/jobs/" + uuid;
            String getResponse = HttpConnectDeal.getFromKylin(jobConfig,kylinGetJobApi);
            boolean getTag = true;
            boolean isReady = false;
            double progress;
            if (getResponse == "" || getResponse == null){
                getTag  = false;
            }else {
                try {
                    JSONObject buildResponseObj = new JSONObject(getResponse);
                    if(buildResponseObj.get("job_status").equals("FINISHED")){
                        isReady = true;
                        progress = buildResponseObj.getDouble("progress");
                        cubeInfo.setProgress(progress);
                    }else if (buildResponseObj.get("job_status").equals("RUNNING")){
                        progress = buildResponseObj.getDouble("progress");
                        cubeInfo.setProgress(progress);
                    }
                } catch (JSONException e) {
                    getTag = false;
                }

            }
            // ------- ends here
            if (!getTag){
                // 查询失败
                tag = false;
                cubeInfo.setStatus("2");
                cubeInfo.setFinishTime(new Date());
            }
            if (isReady){
                // 查询成功 ，已构建完成
                cubeInfo.setStatus("1");
                cubeInfo.setFinishTime(new Date());
                tag = false;
                // 生成 schema 文件 ，并向 saiku 传递 xml
                StringBuilder saiku = new StringBuilder();
                StringReader sr = new StringReader((SchemaUtils.appendSchema(saiku, cubeSchema, cubeSchema.getSchemaDimensions(), cubeSchema.getSchemaMeasureGroups())).toString());
                InputSource is = new InputSource(sr);
                try {
                    //生成saiku的schema文件
                    Document doc = (new SAXBuilder()).build(is);
                    XMLOutputter XMLOut = new XMLOutputter();
                    Format format = Format.getPrettyFormat();
                    format.setEncoding("UTF-8");
                    XMLOut.setFormat(format);
                    XMLOut.output(doc, new FileOutputStream(path + cubeSchema.getName() + ".xml"));

                    //主动向saiku发起POST请求，参数类型为multipart/form-data，实现加入数据模型
                    HttpConnectDeal.postMutilpart(path + cubeSchema.getName() + ".xml", "http://10.1.18.205:8080/saiku/rest/saiku/admin/schema/'" + cubeSchema.getId() + "'", cubeSchema);
                    //根据saiku源码的内容，id，path，advanced三个键的值当不存在时，一定要传NULL，不要传""
                    JSONObject dbconnInfoObj = new JSONObject();
                    dbconnInfoObj.put("id", JSONObject.NULL);
                    dbconnInfoObj.put("password", "KYLIN");
                    dbconnInfoObj.put("username", "admin");
                    dbconnInfoObj.put("path", JSONObject.NULL);
                    dbconnInfoObj.put("schema", "/datasources/" + cubeSchema.getName() + ".xml");
                    dbconnInfoObj.put("driver", "org.apache.kylin.jdbc.Driver");
                    dbconnInfoObj.put("connectionname", cubeSchema.getName());
                    dbconnInfoObj.put("jdbcurl", jobConfig.getKylinJdbc()+jobConfig.getKylinProject());
                    dbconnInfoObj.put("connectiontype", "MONDRIAN");
                    dbconnInfoObj.put("advanced", JSONObject.NULL);
                    HttpConnectDeal.postJson("http://10.1.18.205:8080/saiku/rest/saiku/admin/datasources", dbconnInfoObj);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            // 更新信息

            cubeInfoServiceI.updateCubeInfoByPrimaryKey(cubeInfo);
            try {
                Thread.sleep(timeInterval);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
