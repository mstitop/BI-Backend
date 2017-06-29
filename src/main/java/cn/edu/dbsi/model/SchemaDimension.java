package cn.edu.dbsi.model;

import java.util.List;

public class SchemaDimension {
    private Integer id;

    private String name;

    private String tableName;

    private String key;

    private Integer schemaId;

    private List<SchemaDimensionAttribute> schemaDimensionAttributes;

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

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key == null ? null : key.trim();
    }

    public Integer getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(Integer schemaId) {
        this.schemaId = schemaId;
    }

    public List<SchemaDimensionAttribute> getSchemaDimensionAttributes() {
        return schemaDimensionAttributes;
    }

    public void setSchemaDimensionAttributes(List<SchemaDimensionAttribute> schemaDimensionAttributes) {
        this.schemaDimensionAttributes = schemaDimensionAttributes;
    }
}