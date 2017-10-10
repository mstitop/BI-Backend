package cn.edu.dbsi.controller;

import cn.edu.dbsi.dto.CubeSchema;
import cn.edu.dbsi.interceptor.LoginRequired;
import cn.edu.dbsi.model.*;
import cn.edu.dbsi.service.*;
import cn.edu.dbsi.util.HttpConnectDeal;
import cn.edu.dbsi.util.SchemaUtils;
import cn.edu.dbsi.util.StatusUtil;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileOutputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by 郭世明 on 2017/6/29.
 */
@LoginRequired
@Controller
@RequestMapping(value = "rest")
public class SchemaController {

    @Autowired
    private SchemaServiceI schemaServiceI;

    @Autowired
    private MeasureGroupServiceI measureGroupServiceI;

    @Autowired
    private MeasuresServiceI measuresServiceI;

    @Autowired
    private DimensionServiceI dimensionServiceI;

    @Autowired
    private DimensionAttributeServiceI dimensionAttributeServiceI;

    @Autowired
    private DimensionLinkServiceI dimensionLinkServiceI;

    @Autowired
    private DbConnectionServiceI dbConnectionServiceI;

    @Autowired
    private CubeInfoServiceI cubeInfoServiceI;

    /**
     * 这个controller是用于生成saiku的schema文件
     *
     * @param json
     * @param request
     * @return
     */
    @RequestMapping(value = "/olap-schema", method = RequestMethod.POST)
    @ResponseBody
    public ResponseEntity<?> addBusinessPackage(@RequestBody Map<String, Object> json, HttpServletRequest request) {
        List<SchemaDimension> schemaDimensions = new ArrayList<SchemaDimension>();
        List<SchemaMeasureGroup> schemaMeasureGroups = new ArrayList<SchemaMeasureGroup>();
        List<SchemaDimensionAttribute> schemaDimensionAttributes = new ArrayList<SchemaDimensionAttribute>();
        List<SchemaMeasure> schemaMeasures = new ArrayList<SchemaMeasure>();
        List<SchemaDimensionMeasure> schemaDimensionMeasures = new ArrayList<SchemaDimensionMeasure>();
        StringBuilder sb = new StringBuilder();
        StringBuilder saiku = new StringBuilder();
        int tag = 0, tag2 = 0, tag3 = 0, tag4 = 0, tag5 = 0, tag6 = 0;
        Schema schema = new Schema();
        CubeSchema cubeSchema = new CubeSchema();
        CubeInfo cubeInfo = new CubeInfo();
        JSONObject obj = new JSONObject(json);
        String schemaName = obj.getString("name");
        int bpid = obj.getInt("bpid");
        int bdid = obj.getInt("bdid");
        String description = obj.getString("description");
        cubeSchema.setName(schemaName);
        cubeSchema.setCubeName(schemaName);
        cubeSchema.setBusinessPackageId(bpid);
        schema.setName(schemaName);
        List<String> cubeNames = cubeInfoServiceI.getCubesNames();
        if (cubeNames.contains(schemaName)) {
            return StatusUtil.error("","cube名字不能重复！");
        }
        cubeInfo.setName(schemaName);
        cubeInfo.setBpOrDataxId(bpid);
        cubeInfo.setDescription(description);
        cubeInfo.setCategory("normal");
        cubeInfo.setStatus("1");
        cubeInfo.setIsDelete("0");
        cubeInfo.setCreateTime(new Date());
        cubeInfoServiceI.addCubeInfo(cubeInfo);
        int cubeLastId = cubeInfoServiceI.selectLastCubeInfoPrimaryKey();
        schema.setCubeId(cubeLastId);
        JSONArray tableName = obj.getJSONArray("tableName");
        JSONArray dimensions = obj.getJSONArray("dimensions");
        JSONArray measureGroups = obj.getJSONArray("measureGroups");
        //取出所有table的名字，存入到schema表中
        for (int i = 0; i < tableName.length(); i++) {
            JSONObject table = tableName.getJSONObject(i);
            sb.append(table.getString("name") + ",");
        }
        cubeSchema.setTableNames(sb.toString());
        //将第一个维度名作为default_ measure _name
        cubeSchema.setDefaultMeasureName(measureGroups.getJSONObject(0).getJSONArray("measures").getJSONObject(0).getString("name"));
        cubeSchema.setIsdelete("0");
        schema.setTableNames(sb.toString());
        schema.setDefaultMeasureName(measureGroups.getJSONObject(0).getJSONArray("measures").getJSONObject(0).getString("name"));
        schema.setIsdelete("0");
        tag = schemaServiceI.addSchema(schema);
        int schemaLastId = schemaServiceI.getLastSchemaId();
        //设定ID 是为了在生成schema文件时，用来匹配各个节点
        cubeSchema.setId(schemaLastId);
        schema.setId(schemaLastId);
        cubeInfo.setSchemaId(schemaLastId);
        cubeInfo.setId(cubeLastId);

        //解析json数据中的维度和维度属性值
        for (int i = 0; i < dimensions.length(); i++) {
            SchemaDimension schemaDimension = new SchemaDimension();
            JSONObject dimension = dimensions.getJSONObject(i);
            String dimensionName = dimension.getString("name");
            String dimensionTableName = dimension.getString("tableName");
            String key_attribute = dimension.getString("keyAttribute");
            JSONArray attributes = dimension.getJSONArray("attributes");
            schemaDimension.setSchemaId(schemaLastId);
            schemaDimension.setName(dimensionName);
            schemaDimension.setTableName(dimensionTableName);
            schemaDimension.setKey(key_attribute);
            tag2 = dimensionServiceI.addDimension(schemaDimension);
            int dimensionId = dimensionServiceI.getLastDimensionId();
            //设定ID 是为了在生成schema文件时，用来匹配各个节点
            schemaDimension.setId(dimensionId);
            for (int j = 0; j < attributes.length(); j++) {
                SchemaDimensionAttribute schemaDimensionAttribute = new SchemaDimensionAttribute();
                JSONObject attribute = attributes.getJSONObject(j);
                String attributeName = attribute.getString("name");
                String fieldName = attribute.getString("fieldName");
                schemaDimensionAttribute.setDimensionId(dimensionId);
                schemaDimensionAttribute.setName(attributeName);
                schemaDimensionAttribute.setFieldName(fieldName);
                tag3 = dimensionAttributeServiceI.addDimensionAttribute(schemaDimensionAttribute);
                schemaDimensionAttributes.add(schemaDimensionAttribute);
            }
            schemaDimension.setSchemaDimensionAttributes(schemaDimensionAttributes);
            schemaDimensions.add(schemaDimension);
        }

        //解析json数据中有关指标的值
        for (int i = 0; i < measureGroups.length(); i++) {
            SchemaMeasureGroup schemaMeasureGroup = new SchemaMeasureGroup();
            JSONObject measureGroup = measureGroups.getJSONObject(i);
            String measureGroupName = measureGroup.getString("name");
            String measureTableName = measureGroup.getString("tableName");
            JSONArray measures = measureGroup.getJSONArray("measures");
            JSONArray dimensionLinks = measureGroup.getJSONArray("dimensionLinks");
            schemaMeasureGroup.setName(measureGroupName);
            schemaMeasureGroup.setTableName(measureTableName);
            schemaMeasureGroup.setSchemaId(schemaLastId);
            tag4 = measureGroupServiceI.addMeasureGroup(schemaMeasureGroup);
            int measureGroupId = measureGroupServiceI.getLastMeasureGroupId();
            //设定ID 是为了在生成schema文件时，用来匹配各个节点
            schemaMeasureGroup.setId(measureGroupId);
            for (int j = 0; j < measures.length(); j++) {
                SchemaMeasure schemaMeasure = new SchemaMeasure();
                JSONObject measureObj = measures.getJSONObject(j);
                String measureName = measureObj.getString("name");
                String fieldName = measureObj.getString("fieldName");
                String aggregator = measureObj.getString("aggregator");
                String formatStyle = measureObj.getString("formatStyle");
                schemaMeasure.setName(measureName);
                schemaMeasure.setFieldName(fieldName);
                schemaMeasure.setAggregator(aggregator);
                schemaMeasure.setFormatStyle(formatStyle);
                schemaMeasure.setMeasureGroupId(measureGroupId);
                tag5 = measuresServiceI.addMeasures(schemaMeasure);
                schemaMeasures.add(schemaMeasure);
            }


            for (int k = 0; k < dimensionLinks.length(); k++) {
                SchemaDimensionMeasure schemaDimensionMeasure = new SchemaDimensionMeasure();
                JSONObject dimensionLink = dimensionLinks.getJSONObject(k);
                String dimensionName = dimensionLink.getString("dimensionName");
                String foreignKey = dimensionLink.getString("foreignKey");
                String isForeign = new Boolean(dimensionLink.getBoolean("isForeign")).toString();
                schemaDimensionMeasure.setForeignKey(foreignKey);
                schemaDimensionMeasure.setIsForeign(isForeign);
                schemaDimensionMeasure.setDimensionName(dimensionName);
                schemaDimensionMeasure.setMeasureGroupId(measureGroupId);
                tag6 = dimensionLinkServiceI.addDimensionLink(schemaDimensionMeasure);
                schemaDimensionMeasures.add(schemaDimensionMeasure);
            }
            schemaMeasureGroup.setSchemaMeasures(schemaMeasures);
            schemaMeasureGroup.setSchemaDimensionMeasures(schemaDimensionMeasures);
            schemaMeasureGroups.add(schemaMeasureGroup);
        }

        //当所有存入操作都成功的时候，就开始生成schema文件
        if (tag == 1 && tag2 == 1 && tag3 == 1 && tag4 == 1 && tag5 == 1 && tag6 == 1) {
            cubeSchema.setSchemaDimensions(schemaDimensions);
            cubeSchema.setSchemaMeasureGroups(schemaMeasureGroups);
            StringReader sr = new StringReader((SchemaUtils.appendSchema(saiku, cubeSchema, cubeSchema.getSchemaDimensions(), cubeSchema.getSchemaMeasureGroups())).toString());
            InputSource is = new InputSource(sr);
            try {
                String path = request.getSession().getServletContext().getRealPath("/saiku") + File.separator;
                //生成saiku的schema文件
                Document doc = (new SAXBuilder()).build(is);
                XMLOutputter XMLOut = new XMLOutputter();
                Format format = Format.getPrettyFormat();
                format.setEncoding("UTF-8");
                XMLOut.setFormat(format);
                XMLOut.output(doc, new FileOutputStream(path + cubeSchema.getName() + ".xml"));
                cubeSchema.setAddress(path + cubeSchema.getName());
                schema.setAddress(path + cubeSchema.getName());
                schemaServiceI.updateSchema(schema);
                //主动向saiku发起POST请求，参数类型为multipart/form-data，实现加入数据模型
                HttpConnectDeal.postMutilpart(path + cubeSchema.getName() + ".xml", "http://10.1.18.205:8080/saiku/rest/saiku/admin/schema/'" + cubeSchema.getId() + "'", cubeSchema);
                DbconnInfo dbconnInfo = dbConnectionServiceI.getDbconnInfoById(bdid);
                JSONObject dbconnInfoObj = dbconnInfoToObj(dbconnInfo, cubeSchema);
                HttpConnectDeal.postJson("http://10.1.18.205:8080/saiku/rest/saiku/admin/datasources", dbconnInfoObj);
                cubeInfo.setFinishTime(new Date());
                cubeInfo.setProgress(Double.valueOf(100));
                //更新是为了将Schema_id插入
                cubeInfoServiceI.updateCubeInfoByPrimaryKey(cubeInfo);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return StatusUtil.updateOk();
        } else {
            return StatusUtil.error("", "生成schema失败");
        }
    }


    private JSONObject dbconnInfoToObj(DbconnInfo dbconnInfo, CubeSchema schema) {
        //根据saiku源码的内容，id，path，advanced三个键的值当不存在时，一定要传NULL，不要传""
        JSONObject dbconnInfoObj = new JSONObject();
        dbconnInfoObj.put("id", JSONObject.NULL);
        dbconnInfoObj.put("password", dbconnInfo.getPassword());
        dbconnInfoObj.put("username", dbconnInfo.getUsername());
        dbconnInfoObj.put("path", JSONObject.NULL);
        dbconnInfoObj.put("schema", "/datasources/" + schema.getName() + ".xml");
        dbconnInfoObj.put("driver", dbconnInfo.getJdbcname());
        dbconnInfoObj.put("connectionname", schema.getName());
        dbconnInfoObj.put("jdbcurl", dbconnInfo.getUrl());
        dbconnInfoObj.put("connectiontype", "MONDRIAN");
        dbconnInfoObj.put("advanced", JSONObject.NULL);
        return dbconnInfoObj;
    }
}
