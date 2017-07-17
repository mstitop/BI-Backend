package cn.edu.dbsi.model;

import java.util.ArrayList;
import java.util.List;

public class BusinessPackageGroup {
    private Integer id;

    private String name;

    private String isdelete;

    private List<BusinessPackage> businessPackages;

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

    public String getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(String isdelete) {
        this.isdelete = isdelete == null ? null : isdelete.trim();
    }

    public List<BusinessPackage> getBusinessPackages() {
        return businessPackages;
    }

    public void setBusinessPackages(List<BusinessPackage> businessPackages) {
        this.businessPackages = businessPackages;
    }
}