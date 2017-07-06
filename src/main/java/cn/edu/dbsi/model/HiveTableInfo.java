package cn.edu.dbsi.model;

public class HiveTableInfo {
    private Integer id;

    private String tableName;

    private Integer businessPackageId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName == null ? null : tableName.trim();
    }

    public Integer getBusinessPackageId() {
        return businessPackageId;
    }

    public void setBusinessPackageId(Integer businessPackageId) {
        this.businessPackageId = businessPackageId;
    }
}