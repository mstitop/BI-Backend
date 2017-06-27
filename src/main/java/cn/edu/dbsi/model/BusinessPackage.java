package cn.edu.dbsi.model;

import java.util.List;

public class BusinessPackage {
    private Integer id;

    private String name;

    private Integer groupid;

    private String isdelete;

    private List<DbconnInfo> dbconnInfos;

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

    public Integer getGroupid() {
        return groupid;
    }

    public void setGroupid(Integer groupid) {
        this.groupid = groupid;
    }

    public String getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(String isdelete) {
        this.isdelete = isdelete == null ? null : isdelete.trim();
    }

    public List<DbconnInfo> getDbconnInfos() {
        return dbconnInfos;
    }

    public void setDbconnInfos(List<DbconnInfo> dbconnInfos) {
        this.dbconnInfos = dbconnInfos;
    }
}