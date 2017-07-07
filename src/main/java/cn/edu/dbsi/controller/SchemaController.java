package cn.edu.dbsi.controller;

import cn.edu.dbsi.model.*;
import cn.edu.dbsi.service.*;
import cn.edu.dbsi.util.HttpConnectDeal;
import cn.edu.dbsi.util.SchemaUtils;
import org.jdom2.Document;
import org.jdom2.input.SAXBuilder;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.xml.sax.InputSource;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.*;

/**
 * Created by 郭世明 on 2017/6/29.
 */

@Controller
@RequestMapping(value = "{token}")
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

    /**
     * 这个controller是用于生成saiku的schema文件
     * @param token
     * @param json
     * @param request
     * @return
     */
    @RequestMapping(value = "/saikuSchema", method = RequestMethod.POST)
    @ResponseBody
    public Map<String, Object> addBusinessPackage(@PathVariable("token") Integer token, @RequestBody Map<String, Object> json, HttpServletRequest request) {
        Map<String, Object> map = new HashMap<String, Object>();
        List<SchemaDimension> schemaDimensions = new ArrayList<SchemaDimension>();
        List<SchemaMeasureGroup> schemaMeasureGroups = new ArrayList<SchemaMeasureGroup>();
        List<SchemaDimensionAttribute> schemaDimensionAttributes = new ArrayList<SchemaDimensionAttribute>();
        List<SchemaMeasure> schemaMeasures = new ArrayList<SchemaMeasure>();
        List<SchemaDimensionMeasure> schemaDimensionMeasures = new ArrayList<SchemaDimensionMeasure>();
        StringBuilder sb = new StringBuilder();
        StringBuilder saiku = new StringBuilder();
        int tag = 0, tag2 = 0, tag3 = 0, tag4 = 0, tag5 = 0, tag6 = 0;
        Schema schema = new Schema();
        JSONObject obj = new JSONObject(json);
        String schemaName = obj.getString("name");
        int bpid = obj.getInt("bpid");
        schema.setName(schemaName);
        schema.setCubeName(schemaName);
        schema.setBusinessPackageId(bpid);
        JSONArray tableName = obj.getJSONArray("tableName");
        JSONArray dimensions = obj.getJSONArray("dimensions");
        JSONArray measureGroups = obj.getJSONArray("measureGroups");
        //取出所有table的名字，存入到schema表中
        for (int i = 0; i < tableName.length(); i++) {
            JSONObject table = tableName.getJSONObject(i);
            sb.append(table.getString("name") + ",");
        }
        schema.setTableNames(sb.toString());
        //将第一个维度名作为default_ measure _name
        schema.setDefaultMeasureName(measureGroups.getJSONObject(0).getJSONArray("measures").getJSONObject(0).getString("name"));
        schema.setIsdelete("0");
        tag = schemaServiceI.addSchema(schema);
        int schemaLastId = schemaServiceI.getLastSchemaId();
        //设定ID 是为了在生成schema文件时，用来匹配各个节点
        schema.setId(schemaLastId);

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
            map.put("result", 1);
            schema.setSchemaDimensions(schemaDimensions);
            schema.setSchemaMeasureGroups(schemaMeasureGroups);
//            System.out.println((SchemaUtils.appendSchema(saiku, schema, schema.getSchemaDimensions(), schema.getSchemaMeasureGroups())).toString());
            StringReader sr = new StringReader((SchemaUtils.appendSchema(saiku, schema, schema.getSchemaDimensions(), schema.getSchemaMeasureGroups())).toString());
            InputSource is = new InputSource(sr);
            try {
                String path = request.getSession().getServletContext().getRealPath("/saiku") + File.separator;
                //生成saiku的schema文件
                Document doc = (new SAXBuilder()).build(is);
                XMLOutputter XMLOut = new XMLOutputter();
                Format format = Format.getPrettyFormat();
                format.setEncoding("UTF-8");
                XMLOut.setFormat(format);
                XMLOut.output(doc, new FileOutputStream(path + schema.getName() + ".xml"));
                schema.setAddress(path + schema.getName());
                schemaServiceI.updateSchema(schema);
                //主动向saiku发起POST请求，参数类型为multipart/form-data
               HttpConnectDeal.postMutilpart(path + schema.getName() + ".xml","http://10.65.1.92:8080/saiku/rest/saiku/admin/schema/'"+schema.getId()+"'",schema);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            map.put("result", 0);
            map.put("error", "新增失败");
        }
        return map;
    }


}
