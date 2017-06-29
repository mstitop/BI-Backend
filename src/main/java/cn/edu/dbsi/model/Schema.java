package cn.edu.dbsi.model;

public class Schema {
    private Integer id;

    private String name;

    private String tableNames;

    private String cubeName;

    private String defaultMeasureName;

    private String address;

    private String isdelete;

    private Integer businessPackageId;

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
}