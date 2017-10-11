package cn.edu.dbsi.controller;

import cn.edu.dbsi.dataetl.model.*;
import cn.edu.dbsi.dataetl.util.JobConfig;
import cn.edu.dbsi.dataetl.util.KylinCubeRunnable;
import cn.edu.dbsi.dto.CubeSchema;
import cn.edu.dbsi.interceptor.LoginRequired;
import cn.edu.dbsi.model.*;
import cn.edu.dbsi.service.*;
import cn.edu.dbsi.util.DBUtils;
import cn.edu.dbsi.util.HttpConnectDeal;
import cn.edu.dbsi.util.StatusUtil;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.*;

/**
 * Created by Skye on 2017/7/31.
 */

@LoginRequired
@Controller
@RequestMapping(value = "/rest")
public class KylinController {

    @Autowired
    private HiveTableInfoService hiveTableInfoService;
    @Autowired
    private DataxTaskService dataxTaskService;
    @Autowired
    private SchemaServiceI schemaServiceI;
    @Autowired
    private DimensionServiceI dimensionServiceI;
    @Autowired
    private DimensionAttributeServiceI dimensionAttributeServiceI;
    @Autowired
    private MeasureGroupServiceI measureGroupServiceI;
    @Autowired
    private MeasuresServiceI measuresServiceI;
    @Autowired
    private DimensionLinkServiceI dimensionLinkServiceI;
    @Autowired
    private CubeInfoServiceI cubeInfoServiceI;
    @Autowired
    private JobConfig jobConfig;

    @RequestMapping(value = "/olap/task/{taskId}/tables", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getHiveTables(@PathVariable("taskId") Integer taskId) {
        String hiveDbName = "bi_" + taskId;

        List<Map<String, Object>> tablesList = new ArrayList<Map<String, Object>>();

        List<String> tableNames = hiveTableInfoService.selectTableNameBytask(taskId);

        if (tableNames.size() != 0) {

            for (String tableName : tableNames) {
                Map<String, Object> tableMap = new HashMap<String, Object>();
                List<Map<String, Object>> list = DBUtils.listTables(jobConfig, hiveDbName + "." + tableName);
                if (list == null) {
                    return StatusUtil.error("", "获取 hive 表信息出错！");
                }
                tableMap.put("name", tableName);
                tableMap.put("fields", list);

                tablesList.add(tableMap);
            }
            return StatusUtil.querySuccess(tablesList);
        } else {
            return StatusUtil.error("", "此任务没有与之对应的 hive 表！");
        }

    }

    /**
     * 用于向kylin api 传递数据
     * 保存 model 信息 和 cube 信息
     *
     * @param json
     * @param request
     * @return
     */

    @RequestMapping(value = "/kylin/cube", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> createCube(@RequestBody Map<String, Object> json, HttpServletRequest request) {

        KylinCube kylinCube = new KylinCube();
        List<KylinLookup> kylinLookupList = new ArrayList<KylinLookup>();
        List<KylinDimension> kylinDimensionsList = new ArrayList<KylinDimension>();
        List<KylinMeasure> kylinMeasuresList = new ArrayList<KylinMeasure>();

        StringBuilder saiku = new StringBuilder();

        JSONObject obj = new JSONObject(json);
        String cubeName = obj.getString("name");
        List<String> cubeNames = cubeInfoServiceI.getCubesNames();
        if (cubeNames.contains(cubeName)) {
            return StatusUtil.error("","cube名字不能重复！");
        }

        int taskId = obj.getInt("taskId");
        String hiveDbName = "bi_" + taskId;
        String description = obj.getString("description");
        kylinCube.setName(cubeName);
        kylinCube.setTaskId(taskId);
        kylinCube.setDescription(description);

        // 解析 cube table 信息
        JSONObject tablesObj = obj.getJSONObject("tables");

        KylinTable kylinTable = new KylinTable();
        String factTable = hiveDbName + "." + tablesObj.getString("factTable");
        String factTablePrimaryKey = tablesObj.getString("factTableKey");
        kylinTable.setFactTable(factTable);
        kylinTable.setFactTablePrimaryKey(factTablePrimaryKey);
        // 解析 lookup 信息
        JSONArray lookups = tablesObj.getJSONArray("lookups");
        for (int i = 0; i < lookups.length(); i++) {
            JSONObject lookup = lookups.getJSONObject(i);
            KylinLookup kylinLookup = new KylinLookup();
            String lookupName = hiveDbName + "." + lookup.getString("name");
            kylinLookup.setName(lookupName);
            kylinLookup.setForeignKey(lookup.getString("foreignKey"));
            kylinLookup.setPrimaryKey(lookup.getString("primaryKey"));
            kylinLookupList.add(kylinLookup);
        }
        kylinTable.setKylinLookups(kylinLookupList);

        kylinCube.setKylinTables(kylinTable);

        // 解析 dimensions
        JSONArray dimensionsObj = obj.getJSONArray("dimensions");
        for (int i = 0; i < dimensionsObj.length(); i++) {
            KylinDimension kylinDimension = new KylinDimension();
            JSONObject dimensionObj = dimensionsObj.getJSONObject(i);
            kylinDimension.setName(dimensionObj.getString("name"));
            String tableName = hiveDbName + "." + dimensionObj.getString("tableName");
            kylinDimension.setTableName(tableName);
            if(tableName.equals(factTable)){
                kylinDimension.setKeyAttribute(factTablePrimaryKey);
            }else {
                for (KylinLookup kylinLookup:kylinLookupList){
                    if(kylinLookup.getName().equals(tableName)){
                        kylinDimension.setKeyAttribute(kylinLookup.getPrimaryKey());
                    }
                }
            }
            JSONArray columnsObj = dimensionObj.getJSONArray("columns");
            StringBuffer cols = new StringBuffer();
            for (int j = 0; j < columnsObj.length(); j++) {
                JSONObject columnObj = columnsObj.getJSONObject(j);
                cols.append(columnObj.getString("name"));
                cols.append("-");
                cols.append(columnObj.getString("alias"));
                if (j < columnsObj.length() - 1) {
                    cols.append(",");
                }
            }
            kylinDimension.setColumns(cols.toString());
            kylinDimensionsList.add(kylinDimension);
        }
        kylinCube.setKylinDimensionsList(kylinDimensionsList);

        // 解析 measures
        JSONArray measuresObj = obj.getJSONArray("measures");
        for (int i = 0; i < measuresObj.length(); i++) {
            JSONObject measureObj = measuresObj.getJSONObject(i);
            KylinMeasure kylinMeasure = new KylinMeasure();
            kylinMeasure.setName(measureObj.getString("name"));
            kylinMeasure.setExpression(measureObj.getString("expression"));
            kylinMeasure.setParamType(measureObj.getString("paramType"));
            kylinMeasure.setParamValue(measureObj.getString("paramValue"));
            kylinMeasure.setReturnType(measureObj.getString("returnType"));
            kylinMeasuresList.add(kylinMeasure);

        }
        kylinCube.setKylinMeasuresList(kylinMeasuresList);

        // 获取表信息,用于导入 hive 表
        StringBuffer tables = new StringBuffer();
        KylinTable kylinTable1 = kylinCube.getKylinTables();
        List<KylinLookup> kylinLookupList1 = kylinTable1.getKylinLookups();
        for (KylinLookup kylinLookup:kylinLookupList1){
            tables.append(kylinLookup.getName());
            tables.append(",");
        }
        tables.append(kylinTable1.getFactTable());
        String project = jobConfig.getKylinProject();

        int tagLoad = 1,tagSaiku = 1, buildTag = 1;
        String tagModel = "";
        String tagCube = "1";
        // 生成 kylin model json
        JSONObject modelObj = cube2KylinModel(kylinCube);
        System.out.println(modelObj);
        // 生成 Kylin json mode
        JSONObject cubeObj = cube2KylinCube(kylinCube);
        System.out.println(cubeObj);
        // 给 kylin 传递信息

        String kylinLoadHiveApi = jobConfig.getKylinUrl() + "/api/tables/"+tables.toString()+"/"+ project;

        String kylinModelApi = jobConfig.getKylinUrl() + "/api/models";
        String kylinCubeApi = jobConfig.getKylinUrl() + "/api/cubes";

        String loadResponse = HttpConnectDeal.postJson2Kylin(jobConfig,kylinLoadHiveApi,new JSONObject());
        if (loadResponse == "" || loadResponse == null){
            tagLoad  = 0;
        }else {
            try {
                JSONObject loadResponseObj = new JSONObject(loadResponse);
                if (loadResponseObj.getJSONArray("result.unloaded").length() != 0) {
                    tagLoad = 0;
                }
            } catch (JSONException e) {
                tagLoad = 0;
            }

        }

        if(tagLoad == 0){
            return StatusUtil.error("", "导入 Hive 表失败");
        }else {
            String modelResponse = HttpConnectDeal.postJson2Kylin(jobConfig,kylinModelApi,modelObj);
            tagModel = isSuccessful(modelResponse);
        }


        if (!tagModel.equals("1")){
            if(tagModel.equals("0"))
                return StatusUtil.error("", "生成 model 失败");
            else
                return StatusUtil.error("", tagModel);
        }else {
            String cubeResponse = HttpConnectDeal.postJson2Kylin(jobConfig,kylinCubeApi,cubeObj);
            tagCube = isSuccessful(cubeResponse);
        }
        CubeSchema cubeSchema;
        Schema schema;
        CubeInfo cubeInfo;
        String saikuPath = request.getSession().getServletContext().getRealPath("/saiku") + File.separator;

        if (!tagCube.equals("1")){
            if(tagCube.equals("0"))
                return StatusUtil.error("", "生成 cube 失败");
            else
                return StatusUtil.error("", tagCube);
        }else {
            // 将 kylinCube 解析成 schema 形式,并存入数据库

            Map<String,Object> saikuMap = cube2SaikuSchema(kylinCube,saikuPath);
            tagSaiku = (Integer)saikuMap.get("isSuccess");
            cubeSchema = (CubeSchema)saikuMap.get("cubeSchema");
            schema = (Schema)saikuMap.get("schema");
            cubeInfo = (CubeInfo)saikuMap.get("cubeInfo");
        }

        String jobUuid = "";
        if(tagSaiku == 0){
            return StatusUtil.error("", "存储 saiku schema 表失败");
        }else {
            String kylinBuildCubeApi = jobConfig.getKylinUrl() + "/api/cubes/" + cubeName + "/rebuild";

            JSONObject buildJson = new JSONObject();
            buildJson.put("startTime","0");
            buildJson.put("endTime","3153600000000");
            buildJson.put("buildType","BUILD");
            String buildResponse = HttpConnectDeal.putJson2Kylin(jobConfig,kylinBuildCubeApi,buildJson);
            if (buildResponse == "" || buildResponse == null){
                buildTag  = 0;
            }else {
                try {
                    JSONObject buildResponseObj = new JSONObject(buildResponse);
                    jobUuid = buildResponseObj.getString("uuid");
                } catch (JSONException e) {
                    buildTag = 0;
                }

            }
        }

        if (buildTag == 0){
            return StatusUtil.error("", "构建 cube 失败");
        }else {
            // 开启监控 cube 是否构建完成任务执行线程
            Runnable excuteRunnable = new KylinCubeRunnable(jobUuid,cubeInfo,cubeSchema,schemaServiceI,cubeInfoServiceI,jobConfig,saikuPath);
            Thread thread = new Thread(excuteRunnable);
            thread.start();
        }

        return StatusUtil.updateOk();

    }



    /**
     * 1 查询 cube 是否构建完成
     * 2 向 saiku 发送cube schema xml信息
     *
     * 没有写是否构建出错的判断，可以获取构建cube返回的 uuid，其为job id 然后查看 job 状态来判断
     * 或者 查看 cube 信息，其中有 last_build_job_id 字段，但此字段若 Build cube job 为完成则为  null ，还未测试 job 为 error 的情况下 last_build_job_id 是否会显示 job id
     * @param cubeName
     * @return
     */
    @RequestMapping(value = "/kylin/{cubeName}/get", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> getCube(@PathVariable("cubeName") String cubeName) {
        String kylinGetCubeApi = jobConfig.getKylinUrl() + "/api/cubes/" + cubeName;


        String getResponse = HttpConnectDeal.postJson2Kylin(jobConfig,kylinGetCubeApi,new JSONObject());
        int getTag = 1;
        int isReady = 0;
        if (getResponse == "" || getResponse == null){
            getTag  = 0;
        }else {
            try {
                JSONObject buildResponseObj = new JSONObject(getResponse);
                if(buildResponseObj.get("status").equals("READY")){
                    isReady = 1;
                }
            } catch (JSONException e) {
                getTag = 0;
            }

        }

        if (getTag == 0){
            return StatusUtil.error("", "查询 cube 失败");
        }




        Map<String,Object> returnMap = new HashMap<String,Object>();
        returnMap.put("cubeStatus",isReady);

        return StatusUtil.querySuccess(returnMap);

    }


    private Map<String, Object> cube2SaikuSchema(KylinCube kylinCube,String path){
        // saiku xml 需要表名全部大写，去掉  bi_1. 数据库前缀
        // Dimension 需要加入主键
        Map<String,Object> returnMap = new HashMap<String,Object>();
        int tag = 0, tag2 = 0, tag3 = 0, tag4 = 0, tag5 = 0, tag6 = 0;
        List<SchemaDimension> schemaDimensions = new ArrayList<SchemaDimension>();
        List<SchemaMeasureGroup> schemaMeasureGroups = new ArrayList<SchemaMeasureGroup>();

        List<SchemaMeasure> schemaMeasures = new ArrayList<SchemaMeasure>();
        List<SchemaDimensionMeasure> schemaDimensionMeasures = new ArrayList<SchemaDimensionMeasure>();
        Schema schema = new Schema();
        CubeSchema cubeSchema = new CubeSchema();
        CubeInfo cubeInfo = new CubeInfo();

        DataxTask dataxTask = dataxTaskService.getDataxTaskById(kylinCube.getTaskId());
        String schemaName = kylinCube.getName();
        int bpid = dataxTask.getBusinessPackageId();
        // 此时数据库为 hive ，用0标识
        int bdid = 0;

        String description = kylinCube.getDescription();
        cubeSchema.setName(schemaName);
        cubeSchema.setCubeName(schemaName);
        cubeSchema.setBusinessPackageId(bpid);

        schema.setName(schemaName);

        cubeInfo.setName(schemaName);
        cubeInfo.setBpOrDataxId(bpid);
        cubeInfo.setDescription(description);
        cubeInfo.setCategory("kylin");
        cubeInfo.setStatus("2");
        cubeInfo.setIsDelete("0");
        cubeInfo.setCreateTime(new Date());
        cubeInfo.setProgress(0.0);
        cubeInfoServiceI.addCubeInfo(cubeInfo);

        int lastCubeId = cubeInfoServiceI.getLastCubeInfoId();
        cubeInfo.setId(lastCubeId);

        schema.setCubeId(cubeInfoServiceI.selectLastCubeInfoPrimaryKey());

        StringBuilder tableNames = new StringBuilder();
        KylinTable kylinTable = kylinCube.getKylinTables();
        List<KylinLookup> kylinLookups = kylinTable.getKylinLookups();
        //取出所有table的名字，存入到schema表中
        tableNames.append(kylinTable.getFactTable().split("[.]")[1].toUpperCase() + ",");
        int count = 0;
        for (KylinLookup kylinLookup:kylinLookups){
            count ++;
            tableNames.append(kylinLookup.getName().split("[.]")[1].toUpperCase());
            if(count < kylinLookups.size()){
                tableNames.append(",");
            }

        }
        schema.setTableNames(tableNames.toString());
        cubeSchema.setTableNames(tableNames.toString());
        //将第一个指标名作为default_ measure _name
        List<KylinMeasure> kylinMeasuresList = kylinCube.getKylinMeasuresList();
        schema.setDefaultMeasureName(kylinMeasuresList.get(0).getName());
        schema.setIsdelete("0");

        cubeSchema.setDefaultMeasureName(kylinMeasuresList.get(0).getName());
        cubeSchema.setIsdelete("0");
        tag = schemaServiceI.addSchema(schema);
        int schemaLastId = schemaServiceI.getLastSchemaId();
        //设定ID 是为了在生成schema文件时，用来匹配各个节点
        schema.setId(schemaLastId);
        cubeSchema.setId(schemaLastId);
        cubeInfo.setSchemaId(schemaLastId);

        //更新是为了将Schema_id插入
        cubeInfoServiceI.updateCubeInfoByPrimaryKey(cubeInfo);
        // 存储维度和维度属性值
        List<KylinDimension> kylinDimensionList = kylinCube.getKylinDimensionsList();
        for (KylinDimension kylinDimension:kylinDimensionList){
            List<SchemaDimensionAttribute> schemaDimensionAttributes = new ArrayList<SchemaDimensionAttribute>();
            SchemaDimension schemaDimension = new SchemaDimension();
            String dimensionName = kylinDimension.getName();
            String dimensionTableName = kylinDimension.getTableName().split("[.]")[1].toUpperCase();
            String key_attribute = kylinDimension.getKeyAttribute();
            schemaDimension.setSchemaId(schemaLastId);
            schemaDimension.setName(dimensionName);
            schemaDimension.setTableName(dimensionTableName);

            tag2 = dimensionServiceI.addDimension(schemaDimension);
            int dimensionId = dimensionServiceI.getLastDimensionId();
            schemaDimension.setId(dimensionId);

            String colStr = kylinDimension.getColumns();
            String[] colsStr = colStr.split(",");
            boolean isExist = false;
            for (String col : colsStr) {
                SchemaDimensionAttribute schemaDimensionAttribute = new SchemaDimensionAttribute();
                String attributeName = col.split("-")[1];
                String fieldName = col.split("-")[0];
                boolean isKey = false;
                if (fieldName.equalsIgnoreCase(kylinTable.getFactTablePrimaryKey())) {
                    isExist = true;
                    isKey = true;
                }
                for (KylinLookup kylinLookup : kylinLookups) {
                    if (fieldName.equalsIgnoreCase(kylinLookup.getPrimaryKey())) {
                        isExist = true;
                        isKey = true;
                    }
                }
                schemaDimensionAttribute.setDimensionId(dimensionId);
                schemaDimensionAttribute.setName(attributeName);
                schemaDimensionAttribute.setFieldName(fieldName);
                tag3 = dimensionAttributeServiceI.addDimensionAttribute(schemaDimensionAttribute);
                schemaDimensionAttributes.add(schemaDimensionAttribute);

                // saiku 的维度 Key 属性是 字段别名，当传入的 kylin cube 有 主键维的时候 Key = 别名，否则 key = 字段名
                if (isKey){
                    schemaDimension.setKey(attributeName);
                }
            }
            // 给当前维度增加，相应表的主键属性
            SchemaDimensionAttribute schemaDimensionAttribute = new SchemaDimensionAttribute();
            String attributeName = "";
            String fieldName = "";
            if (!isExist) {
                if(kylinDimension.getTableName().equals(kylinTable.getFactTable())){
                    attributeName = kylinTable.getFactTablePrimaryKey();
                    fieldName = kylinTable.getFactTablePrimaryKey();
                    schemaDimension.setKey(attributeName);
                }else {
                    for (KylinLookup kylinLookup:kylinLookups){
                        if (kylinDimension.getTableName().equals(kylinLookup.getName())){
                            attributeName = kylinLookup.getPrimaryKey();
                            fieldName = kylinLookup.getPrimaryKey();
                            schemaDimension.setKey(attributeName);
                            break;
                        }
                    }
                }
                schemaDimensionAttribute.setDimensionId(dimensionId);
                schemaDimensionAttribute.setName(attributeName);
                schemaDimensionAttribute.setFieldName(fieldName);
                tag3 = dimensionAttributeServiceI.addDimensionAttribute(schemaDimensionAttribute);
                schemaDimensionAttributes.add(schemaDimensionAttribute);
            }

            schemaDimension.setSchemaDimensionAttributes(schemaDimensionAttributes);
            schemaDimensions.add(schemaDimension);
        }
        //存储有关指标的值
        SchemaMeasureGroup schemaMeasureGroup = new SchemaMeasureGroup();
        schemaMeasureGroup.setName(kylinTable.getFactTable().split("[.]")[1].toUpperCase());
        schemaMeasureGroup.setTableName(kylinTable.getFactTable().split("[.]")[1].toUpperCase());
        schemaMeasureGroup.setSchemaId(schemaLastId);
        tag4 = measureGroupServiceI.addMeasureGroup(schemaMeasureGroup);
        int measureGroupId = measureGroupServiceI.getLastMeasureGroupId();
        schemaMeasureGroup.setId(measureGroupId);
        for(KylinMeasure kylinMeasure:kylinMeasuresList){

            if (kylinMeasure.getName().equals("_COUNT_")||kylinMeasure.getParamType().equals("constant")) {
                continue;
            }
            SchemaMeasure schemaMeasure = new SchemaMeasure();

            String measureName = kylinMeasure.getName();
            String fieldName = kylinMeasure.getParamValue();
            String aggregator = kylinMeasure.getExpression();
            if (aggregator.equals("COUNT_DISTINCT")){
                aggregator = "distinct-count";
            }
            // 将计算方法改为 saiku 支持的方法
            aggregator = aggregator.toLowerCase();
            String formatStyle = "Standard";
            schemaMeasure.setName(measureName);
            schemaMeasure.setFieldName(fieldName);
            schemaMeasure.setAggregator(aggregator);
            schemaMeasure.setFormatStyle(formatStyle);
            schemaMeasure.setMeasureGroupId(measureGroupId);
            tag5 = measuresServiceI.addMeasures(schemaMeasure);
            schemaMeasures.add(schemaMeasure);
        }
        for (KylinDimension kylinDimension:kylinDimensionList){
            SchemaDimensionMeasure schemaDimensionMeasure = new SchemaDimensionMeasure();
            String dimensionName = kylinDimension.getName();
            String foreignKey = kylinDimension.getKeyAttribute();
            String isForeign = "";
            if(kylinDimension.getTableName().equals(kylinTable.getFactTable())){
                isForeign = "false";
            }else {
                isForeign = "true";
            }
            schemaDimensionMeasure.setForeignKey(foreignKey);
            schemaDimensionMeasure.setIsForeign(isForeign);
            schemaDimensionMeasure.setDimensionName(dimensionName);
            schemaDimensionMeasure.setMeasureGroupId(measureGroupId);
            for(SchemaDimension schemaDimension: schemaDimensions){
                if (schemaDimension.getTableName().equalsIgnoreCase(kylinDimension.getTableName())){
                    schemaDimensionMeasure.setDimensionId(schemaDimension.getId());
                    break;
                }
            }
            tag6 = dimensionLinkServiceI.addDimensionLink(schemaDimensionMeasure);
            schemaDimensionMeasures.add(schemaDimensionMeasure);
        }
        schemaMeasureGroup.setSchemaMeasures(schemaMeasures);
        schemaMeasureGroup.setSchemaDimensionMeasures(schemaDimensionMeasures);
        schemaMeasureGroups.add(schemaMeasureGroup);
        if (tag == 1 && tag2 == 1 && tag3 == 1 && tag4 == 1 && tag5 == 1 && tag6 == 1) {
            cubeSchema.setSchemaDimensions(schemaDimensions);
            cubeSchema.setSchemaMeasureGroups(schemaMeasureGroups);
            schema.setSchemaDimensions(schemaDimensions);
            schema.setSchemaMeasureGroups(schemaMeasureGroups);
            cubeSchema.setAddress(path + cubeSchema.getName());
            schema.setAddress(path + cubeSchema.getName());
            schemaServiceI.updateSchema(schema);
            returnMap.put("isSuccess",1);
            returnMap.put("cubeSchema",cubeSchema);
            returnMap.put("schema",schema);
            returnMap.put("cubeInfo",cubeInfo);
            return returnMap;
        }else {
            returnMap.put("isSuccess",0);
            return returnMap;
        }
    }
    private String isSuccessful(String response){
        int tag = 1;
        boolean hasException = false;
        String exception = "";
        if (response == "" || response == null){
            tag  = 0;
        }else {
            try {
                JSONObject modelResponseObj = new JSONObject(response);
                if (modelResponseObj.has("exception")){
                    hasException = true;
                    tag = 0;
                    exception = modelResponseObj.getString("exception");
                }
                if (modelResponseObj.has("successful") && !modelResponseObj.getBoolean("successful")) {
                    tag = 0;
                }

            } catch (JSONException e) {
                tag = 0;
            }

        }
        if (tag == 1)
            return "1";
        else if (tag == 0 && hasException)
            return exception;
        else
            return "0";
    }
    private JSONObject cube2KylinModel(KylinCube kylinCube) {
        JSONObject modelObj = new JSONObject();
        modelObj.put("name", kylinCube.getName());
        modelObj.put("owner", jobConfig.getKylinOwner());
        modelObj.put("description", kylinCube.getDescription());
        KylinTable kylinTable = kylinCube.getKylinTables();
        modelObj.put("fact_table", kylinTable.getFactTable());

        List<KylinLookup> kylinLookupList = kylinTable.getKylinLookups();
        JSONArray lookupsArr = new JSONArray();
        for (KylinLookup kylinLookup : kylinLookupList) {
            JSONObject lookupObj = new JSONObject();
            lookupObj.put("table", kylinLookup.getName());
            JSONObject joinObj = new JSONObject();
            joinObj.put("type", "inner");
            List<String> primary_key = new ArrayList<String>();
            primary_key.add(kylinLookup.getPrimaryKey());
            List<String> foreign_key = new ArrayList<String>();
            foreign_key.add(kylinLookup.getForeignKey());
            joinObj.put("primary_key", primary_key);
            joinObj.put("foreign_key", foreign_key);
            lookupObj.put("join", joinObj);

            lookupsArr.put(lookupObj);
        }
        modelObj.put("lookups", lookupsArr);

        List<KylinDimension> kylinDimensionList = kylinCube.getKylinDimensionsList();
        JSONArray dimensionsArr = new JSONArray();

        for (KylinDimension kylinDimension : kylinDimensionList) {
            JSONObject dimensionObj = new JSONObject();
            dimensionObj.put("table", kylinDimension.getTableName());
            List<String> columns = new ArrayList<String>();
            String colStr = kylinDimension.getColumns();
            String[] colsStr = colStr.split(",");
            for (String col : colsStr) {
                columns.add(col.split("-")[0]);
            }
            dimensionObj.put("columns", columns);

            dimensionsArr.put(dimensionObj);
        }

        modelObj.put("dimensions", dimensionsArr);

        List<KylinMeasure> kylinMeasureList = kylinCube.getKylinMeasuresList();
        List<String> metricsList = new ArrayList<String>();
        for (KylinMeasure kylinMeasure : kylinMeasureList) {
            if (!kylinMeasure.getParamValue().equals("1")) {
                metricsList.add(kylinMeasure.getParamValue());
            }
        }
        modelObj.put("metrics", metricsList);
        modelObj.put("filter_condition", "");
        modelObj.put("capacity", "MEDIUM");
        JSONObject partitionObj = new JSONObject();
        partitionObj.put("partition_date_start", 0);
        partitionObj.put("partition_date_format", "yyyy-MM-dd HH:mm:ss");
        partitionObj.put("partition_time_format", "HH:mm:ss");
        partitionObj.put("artition_type", "APPEND");
        partitionObj.put("partition_condition_builder", "org.apache.kylin.metadata.model.PartitionDesc$DefaultPartitionConditionBuilder");

        modelObj.put("partition_desc", partitionObj);

        JSONObject modelObjFianl = new JSONObject();
        modelObjFianl.put("project",jobConfig.getKylinProject());
        modelObjFianl.put("modelName",kylinCube.getName());
        modelObjFianl.put("modelDescData",modelObj.toString());
        return modelObjFianl;
    }

    private JSONObject cube2KylinCube(KylinCube kylinCube) {
        JSONObject cubeObj = new JSONObject();

        cubeObj.put("name", kylinCube.getName());
        cubeObj.put("model_name", kylinCube.getName());
        cubeObj.put("description", kylinCube.getDescription());

        KylinTable kylinTable = kylinCube.getKylinTables();
        String fact_table = kylinTable.getFactTable();
        List<KylinDimension> kylinDimensionList = kylinCube.getKylinDimensionsList();
        JSONArray dimensionsArr = new JSONArray();

        JSONArray rowColArr = new JSONArray();
        List<String> includesList = new ArrayList<String>();

        for (KylinDimension kylinDimension : kylinDimensionList) {
            String colStr = kylinDimension.getColumns();
            String[] colsStr = colStr.split(",");
            for (String col : colsStr) {
                JSONObject dimensionObj = new JSONObject();
                dimensionObj.put("table", kylinDimension.getTableName());
                String column = col.split("-")[0];
                String name = col.split("-")[1];
                dimensionObj.put("name", name);
                if (kylinDimension.getTableName().equals(fact_table)) {
                    dimensionObj.put("column", column);
                    // 将 维度中属于事实表的维度列都加到 row key  和 aggregation_groups 中
                    JSONObject rowcolObj = new JSONObject();
                    rowcolObj.put("column", column);
                    rowcolObj.put("encoding", "dict");
                    rowcolObj.put("isShardBy", false);
                    rowColArr.put(rowcolObj);
                    includesList.add(column);
                } else {
                    List<String> list = new ArrayList<String>();
                    list.add(column);
                    dimensionObj.put("derived", list);
                }

                dimensionsArr.put(dimensionObj);
            }
        }
        cubeObj.put("dimensions", dimensionsArr);

        List<KylinMeasure> kylinMeasureList = kylinCube.getKylinMeasuresList();
        JSONArray measuresArr = new JSONArray();

        // hbase_map 用到 measure 字段，将 _count_ 和其它非 count 度量放在 F1，将除 _count_ 之外的 count 度量放在 F2
        List<String> hbase_map_f1_list = new ArrayList<String>();
        hbase_map_f1_list.add("_COUNT_");
        List<String> hbase_map_f2_list = new ArrayList<String>();
        for (KylinMeasure kylinMeasure : kylinMeasureList) {
            if (kylinMeasure.getExpression().equalsIgnoreCase("count")){
                continue;
            }
            JSONObject measureObj = new JSONObject();
            measureObj.put("name", kylinMeasure.getName());
            JSONObject funcObj = new JSONObject();
            JSONObject paraObj = new JSONObject();
            paraObj.put("type", kylinMeasure.getParamType());
            paraObj.put("value", kylinMeasure.getParamValue());

            funcObj.put("expression", kylinMeasure.getExpression());
            funcObj.put("returntype", kylinMeasure.getReturnType());
            funcObj.put("parameter",paraObj);

            measureObj.put("function", funcObj);
            measuresArr.put(measureObj);

            if (kylinMeasure.getExpression().equals("COUNT_DISTINCT")) {
                hbase_map_f2_list.add(kylinMeasure.getName());
            } else {
                hbase_map_f1_list.add(kylinMeasure.getName());
            }
        }
        JSONObject measureObj = new JSONObject();
        measureObj.put("name", "_COUNT_");
        JSONObject funcObj = new JSONObject();
        JSONObject paraObj = new JSONObject();
        paraObj.put("type", "constant");
        paraObj.put("value", "1");

        funcObj.put("expression", "COUNT");
        funcObj.put("returntype", "bigint");
        funcObj.put("parameter",paraObj);

        measureObj.put("function", funcObj);

        measuresArr.put(measureObj);

        cubeObj.put("measures", measuresArr);
        List<String> dictionariesTempList = new ArrayList<String>();
        cubeObj.put("dictionaries", dictionariesTempList);

        // 在 rowkey 中增加 事实表中未出现的维表中的主键字段
        List<KylinLookup> kylinLookupList = kylinTable.getKylinLookups();
        for (KylinLookup kylinLookup : kylinLookupList) {
            String column = kylinLookup.getForeignKey();
            boolean flag = false;
            for (int i = 0; i < rowColArr.length(); i++) {
                JSONObject rowKey = rowColArr.getJSONObject(i);
                if (column.equals(rowKey.getString("column"))) {
                    flag = true;
                }
            }
            if (flag) {
                continue;
            }
            JSONObject rowcolObj = new JSONObject();
            rowcolObj.put("column", column);
            rowcolObj.put("encoding", "dict");
            rowcolObj.put("isShardBy", false);
            rowColArr.put(rowcolObj);
        }
        JSONObject rowkeyObj = new JSONObject();
        rowkeyObj.put("rowkey_columns", rowColArr);

        cubeObj.put("rowkey", rowkeyObj);

        JSONArray agg_groupArr = new JSONArray();
        JSONObject aggregation_groupsObj = new JSONObject();
        aggregation_groupsObj.put("includes", includesList);
        JSONObject select_ruleObj = new JSONObject();
        select_ruleObj.put("hierarchy_dims", new ArrayList<String>());
        select_ruleObj.put("mandatory_dims", new ArrayList<String>());
        select_ruleObj.put("joint_dims", new ArrayList<String>());
        aggregation_groupsObj.put("select_rule", select_ruleObj);
        agg_groupArr.put(aggregation_groupsObj);
        cubeObj.put("aggregation_groups", agg_groupArr);

        JSONObject hbase_mapping = new JSONObject();
        JSONArray colFamilyArr = new JSONArray();
        JSONObject f1Obj = new JSONObject();
        f1Obj.put("name", "F1");
        JSONArray f1_col_Arr = new JSONArray();
        JSONObject f1_col_obj = new JSONObject();
        f1_col_obj.put("qualifier", "M");
        f1_col_obj.put("measure_refs", hbase_map_f1_list);
        f1_col_Arr.put(f1_col_obj);
        f1Obj.put("columns", f1_col_Arr);
        colFamilyArr.put(f1Obj);
        if (hbase_map_f2_list.size() != 0) {
            JSONObject f2Obj = new JSONObject();
            f2Obj.put("name", "F2");
            JSONArray f2_col_Arr = new JSONArray();
            JSONObject f2_col_obj = new JSONObject();
            f2_col_obj.put("qualifier", "M");
            f2_col_obj.put("measure_refs", hbase_map_f2_list);
            f2_col_Arr.put(f2_col_obj);
            f2Obj.put("columns", f2_col_Arr);
            colFamilyArr.put(f2Obj);
        }
        hbase_mapping.put("column_family", colFamilyArr);
        cubeObj.put("hbase_mapping",hbase_mapping);

        // 额外默认配置
        cubeObj.put("notify_list",new ArrayList<Object>());
        List<String> status_need_notify = new ArrayList<String>();
        status_need_notify.add("ERROR");
        status_need_notify.add("DISCARDED");
        status_need_notify.add("SUCCEED");
        cubeObj.put("status_need_notify",status_need_notify);
        cubeObj.put("partition_date_start",0);
        cubeObj.put("partition_date_end",3153600000000L);
        List<Long> auto_merge_time_ranges = new ArrayList<Long>();
        auto_merge_time_ranges.add(604800000L);
        auto_merge_time_ranges.add(2419200000L);
        cubeObj.put("auto_merge_time_ranges",auto_merge_time_ranges);
        cubeObj.put("retention_range",0);
        cubeObj.put("engine_type",2);
        cubeObj.put("storage_type",2);
        cubeObj.put("override_kylin_properties",new JSONObject());
        System.out.println(cubeObj);

        JSONObject cubeObjFianl = new JSONObject();
        cubeObjFianl.put("project",jobConfig.getKylinProject());
        cubeObjFianl.put("cubeName",kylinCube.getName());
        cubeObjFianl.put("cubeDescData",cubeObj.toString());
        return cubeObjFianl;
    }
}
