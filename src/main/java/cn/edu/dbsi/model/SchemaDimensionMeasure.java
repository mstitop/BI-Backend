package cn.edu.dbsi.model;

public class SchemaDimensionMeasure {
    private Integer id;

    private Integer dimensionId;

    private String dimensionName;

    private Integer measureGroupId;

    private String foreignKey;

    private String isForeign;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getDimensionId() {
        return dimensionId;
    }

    public void setDimensionId(Integer dimensionId) {
        this.dimensionId = dimensionId;
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

    public String getDimensionName() {
        return dimensionName;
    }

    public void setDimensionName(String dimensionName) {
        this.dimensionName = dimensionName;
    }
}