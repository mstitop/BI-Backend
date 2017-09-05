package cn.edu.dbsi.dto;

import cn.edu.dbsi.model.SchemaDimension;
import cn.edu.dbsi.model.SchemaMeasureGroup;

import java.util.List;

public class CubeSchema {
    private Integer id;

    private String name;

    private String tableNames;

    private String cubeName;

    private String defaultMeasureName;

    private String address;

    private String isdelete;

    private Integer businessPackageId;

    private List<SchemaMeasureGroup> schemaMeasureGroups;

    private List<SchemaDimension> schemaDimensions;

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

    public String getTableNames() {
        return tableNames;
    }

    public void setTableNames(String tableNames) {
        this.tableNames = tableNames == null ? null : tableNames.trim();
    }

    public String getCubeName() {
        return cubeName;
    }

    public void setCubeName(String cubeName) {
        this.cubeName = cubeName == null ? null : cubeName.trim();
    }

    public String getDefaultMeasureName() {
        return defaultMeasureName;
    }

    public void setDefaultMeasureName(String defaultMeasureName) {
        this.defaultMeasureName = defaultMeasureName == null ? null : defaultMeasureName.trim();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address == null ? null : address.trim();
    }

    public String getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(String isdelete) {
        this.isdelete = isdelete == null ? null : isdelete.trim();
    }

    public Integer getBusinessPackageId() {
        return businessPackageId;
    }

    public void setBusinessPackageId(Integer businessPackageId) {
        this.businessPackageId = businessPackageId;
    }

    public List<SchemaMeasureGroup> getSchemaMeasureGroups() {
        return schemaMeasureGroups;
    }

    public void setSchemaMeasureGroups(List<SchemaMeasureGroup> schemaMeasureGroups) {
        this.schemaMeasureGroups = schemaMeasureGroups;
    }

    public List<SchemaDimension> getSchemaDimensions() {
        return schemaDimensions;
    }

    public void setSchemaDimensions(List<SchemaDimension> schemaDimensions) {
        this.schemaDimensions = schemaDimensions;
    }
}