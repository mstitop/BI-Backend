package cn.edu.dbsi.controller;

import cn.edu.dbsi.dataetl.model.*;
import cn.edu.dbsi.dataetl.util.JobConfig;
import cn.edu.dbsi.interceptor.LoginRequired;
import cn.edu.dbsi.service.HiveTableInfoService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
                tableMap.put("fileds", list);

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

        JSONObject obj = new JSONObject(json);
        String cubeName = obj.getString("name");
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
        kylinTable.setFactTable(factTable);
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
            kylinDimension.setKeyAttribute(dimensionObj.getString("keyAttribute"));

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

        int tagModel = 1,tagCube = 1;
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

        String modelResponse = HttpConnectDeal.postJson2Kylin(jobConfig,kylinModelApi,modelObj);
        System.out.println(modelResponse);
        if (modelResponse == "" || modelResponse == null){
            tagModel  = 0;
        }else {
            try{
                JSONObject modelResponseObj = new JSONObject(modelResponse);
                if (!modelResponseObj.getBoolean("successful")){
                    tagModel  = 0;
                }
            }catch (JSONException e){
                tagModel  = 0;
            }
        }
        if (tagModel == 0){
            return StatusUtil.error("", "生成 model 失败");
        }else {
            String cubeResponse = HttpConnectDeal.postJson2Kylin(jobConfig,kylinCubeApi,cubeObj);
            if (cubeResponse == "" || cubeResponse == null){
                tagCube  = 0;
            }else {
                try{
                    JSONObject cubeResponseObj = new JSONObject(cubeResponse);
                    if (!cubeResponseObj.getBoolean("successful")){
                        tagCube  = 0;
                    }
                }catch (JSONException e){
                    tagCube  = 0;
                }
            }
        }
        if (tagCube == 0){
            return StatusUtil.error("", "生成 cube 失败");
        }else {
            // 将 kylinCube 解析成 schema 形式

            // 给 saiku 传递 schema.xml
        }


        return StatusUtil.updateOk();
    }

    /**
     * 执行构建 cube
     * @param cubeName
     * @return
     */
    @RequestMapping(value = "/kylin/{cubeName}/build", method = RequestMethod.GET)
    @ResponseBody
    public ResponseEntity<?> builtCube(@PathVariable("cubeName") String cubeName) {


            return null;


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
