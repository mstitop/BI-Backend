package cn.edu.dbsi.model;

public class SchemaDimensionMeasure {
    private Integer id;

    private Integer schemaId;

    private Integer measureGroupId;

    private String foreignKey;

    private String isForeign;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getSchemaId() {
        return schemaId;
    }

    public void setSchemaId(Integer schemaId) {
        this.schemaId = schemaId;
    }

    public Integer getMeasureGroupId() {
        return measureGroupId;
    }

    public void setMeasureGroupId(Integer measureGroupId) {
        this.measureGroupId = measureGroupId;
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey == null ? null : foreignKey.trim();
    }

    public String getIsForeign() {
        return isForeign;
    }

    public void setIsForeign(String isForeign) {
        this.isForeign = isForeign == null ? null : isForeign.trim();
    }
}