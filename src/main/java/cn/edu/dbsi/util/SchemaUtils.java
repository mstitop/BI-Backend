package cn.edu.dbsi.util;

import cn.edu.dbsi.dto.CubeSchema;
import cn.edu.dbsi.model.*;

import java.util.List;


/**
 * Created by 郭世明 on 2017/6/28.
 */
public class SchemaUtils {

    private static String newLine = "\r\n";

    public static String createSchema(CubeSchema schema, List<SchemaDimension> dimensions, List<SchemaMeasureGroup> measures) {
        StringBuilder sb = new StringBuilder();
        sb = appendSchema(sb, schema, dimensions, measures);
        return sb.toString();
    }

    public static StringBuilder appendSchema(StringBuilder sb, CubeSchema schema, List<SchemaDimension> dimensions, List<SchemaMeasureGroup> measures) {
        sb.append("<?xml version='1.0'?>").append(newLine)
                .append("<Schema name='" + schema.getName() + "' metamodelVersion='4.0'>")
                .append(newLine);
        sb = appendTable(sb, schema);
        sb = appendCube(sb, schema, measures, dimensions);
        sb.append("</Schema>").append(newLine);
        return sb;
    }

    /**
     * 生成PhysicalSchema
     *
     * @param sb
     * @param schema
     * @return
     */
    public static StringBuilder appendTable(StringBuilder sb, CubeSchema schema) {
        String[] tables = schema.getTableNames().split(",");
        sb.append("<PhysicalSchema>").append(newLine);
        for (int i = 0; i < tables.length; i++) {
            sb.append("<Table name='" + tables[i] + "'/>").append(newLine);
        }
        sb.append("</PhysicalSchema>").append(newLine);
        return sb;
    }

    /**
     * 生成cube
     *
     * @param sb
     * @param schema
     * @param measureDescList
     * @param dimensionDescList
     * @return
     */
    public static StringBuilder appendCube(StringBuilder sb, CubeSchema schema, List<SchemaMeasureGroup> measureDescList, List<SchemaDimension> dimensionDescList) {
        sb.append("<Cube name='" + schema.getCubeName() + "' defaultMeasure ='" + schema.getDefaultMeasureName() + "'>").append(newLine);
        sb = appendDimension(sb, dimensionDescList);
        sb.append("<MeasureGroups>").append(newLine);
        for (SchemaMeasureGroup measure : measureDescList) {
            sb.append("<MeasureGroup table='" + measure.getTableName() + "' name='" + measure.getName() + "'>").append(newLine);
            sb.append("<Measures>").append(newLine);
            for (SchemaMeasure measureDesc : measure.getSchemaMeasures()) {
                //对包装类使用==运算符时需要特别注意
                if (measure.getId().intValue() == measureDesc.getMeasureGroupId().intValue()) {
                    sb.append("<Measure aggregator='" + measureDesc.getAggregator() + "' column='" + measureDesc.getFieldName() + "' name='" + measureDesc.getName() + "' formatString='" + measureDesc.getFormatStyle() + "'/>")
                            .append(newLine);
                }
            }
            sb.append("</Measures>").append(newLine);
            sb.append("<DimensionLinks>").append(newLine);
            for (SchemaDimensionMeasure dimensionDesc : measure.getSchemaDimensionMeasures()) {
                if (measure.getId().intValue() == dimensionDesc.getMeasureGroupId().intValue()) {
                    if ("false".equals(dimensionDesc.getIsForeign())) {
                        sb.append("<FactLink dimension='" + dimensionDesc.getDimensionName() + "'/>").append(newLine);
                    } else {
                        sb.append("<ForeignKeyLink dimension='" + dimensionDesc.getDimensionName() + "' foreignKeyColumn='" + dimensionDesc.getForeignKey() + "'/>").append(newLine);
                    }
                }
            }
            sb.append(" </DimensionLinks>").append(newLine);
            sb.append("</MeasureGroup>").append(newLine);
        }
        sb.append("</MeasureGroups>").append(newLine);
        sb.append("</Cube>").append(newLine);
        return sb;
    }

    /**
     * 生成Dimensions
     *
     * @param sb
     * @param dimensionDescList
     * @return
     */
    public static StringBuilder appendDimension(StringBuilder sb, List<SchemaDimension> dimensionDescList) {
        sb.append("<Dimensions>").append(newLine);
        for (SchemaDimension dimensionDesc : dimensionDescList) {
            sb.append("<Dimension name='" + dimensionDesc.getName() + "' key='" + dimensionDesc.getKey() + "' table='" + dimensionDesc.getTableName() + "'>").append(newLine);
            sb.append("<Attributes>").append(newLine);
            for (SchemaDimensionAttribute column : dimensionDesc.getSchemaDimensionAttributes()) {
                if (column.getDimensionId().intValue() == dimensionDesc.getId().intValue()) {
                    if (!column.getName().equals((dimensionDesc.getKey()))) {
                        // add Attributes to stringbuffer
                        sb = addAttribute(sb, column.getName(), column.getFieldName());
                    } else {
                        sb = addAttribute2(sb, column.getName(), column.getFieldName());
                    }
                }
            }
            sb.append("</Attributes>").append(newLine);
            sb.append("</Dimension>").append(newLine);
        }
        sb.append("</Dimensions>").append(newLine);
        return sb;
    }

    /**
     * 加入维度的属性
     *
     * @param sb
     * @param name
     * @param attr
     * @return
     */
    public static StringBuilder addAttribute(StringBuilder sb, String name, String attr) {
        sb.append("<Attribute hasHierarchy='true' levelType='Regular' name='" + name + "' keyColumn='" + attr + "'/>").append(newLine);
        System.out.println(sb.toString());
//                .append("<Key>").append(newLine)
//                .append("<Column name='" + attr + "'/>").append(newLine)
//                .append("</Key>").append(newLine)
//                .append("</Attribute>").append(newLine);
        return sb;
    }

    public static StringBuilder addAttribute2(StringBuilder sb, String name, String attr) {
        sb.append("<Attribute hasHierarchy='false' levelType='Regular' name='" + name + "' keyColumn='" + attr + "'/>").append(newLine);
//                .append("<Key>").append(newLine)
//                .append("<Column name='" + attr + "'/>").append(newLine)
//                .append("</Key>").append(newLine)
//                .append("</Attribute>").append(newLine);
        return sb;
    }

    public static StringBuilder addDimensionLink(StringBuilder sb, List<SchemaDimensionMeasure> schemaDimensionMeasures) {
        sb.append("<DimensionLinks>").append(newLine);
        for (SchemaDimensionMeasure dimensionDesc : schemaDimensionMeasures) {
            if ("0".equals(dimensionDesc.getIsForeign())) {
                sb.append("<FactLink dimension='" + dimensionDesc.getDimensionName() + "'/>").append(newLine);
            } else {
                sb.append("<ForeignKeyLink dimension='" + dimensionDesc.getDimensionName() + "' foreignKeyColumn='" + dimensionDesc.getForeignKey() + "'/>").append(newLine);
            }
        }
        sb.append(" </DimensionLinks>").append(newLine);
        return sb;
    }

}
