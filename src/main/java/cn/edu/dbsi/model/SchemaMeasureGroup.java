package cn.edu.dbsi.model;

import java.util.List;

public class SchemaMeasureGroup {
    private Integer id;

    private String name;

    private String tableName;

    private Integer schemaId;

    private List<SchemaMeasure> schemaMeasures;

    private List<SchemaDimensionMeasure> schemaDimensionMeasures;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName == null ? null : tableName.trim();
    }

    public Integer getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(Integer schemaId) {
        this.schemaId = schemaId;
    }

    public List<SchemaMeasure> getSchemaMeasures() {
        return schemaMeasures;
    }

    public void setSchemaMeasures(List<SchemaMeasure> schemaMeasures) {
        this.schemaMeasures = schemaMeasures;
    }

    public List<SchemaDimensionMeasure> getSchemaDimensionMeasures() {
        return schemaDimensionMeasures;
    }

    public void setSchemaDimensionMeasures(List<SchemaDimensionMeasure> schemaDimensionMeasures) {
        this.schemaDimensionMeasures = schemaDimensionMeasures;
    }
}