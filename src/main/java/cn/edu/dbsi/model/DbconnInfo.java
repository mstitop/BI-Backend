package cn.edu.dbsi.model;

import java.util.List;

public class DbconnInfo {
    private Integer id;

    private String name;

    private String url;

    private String category;

    private String username;

    private String password;

    private String jdbcname;

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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url == null ? null : url.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category == null ? null : category.trim();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username == null ? null : username.trim();
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password == null ? null : password.trim();
    }

    public String getJdbcname() {
        return jdbcname;
    }

    public void setJdbcname(String jdbcname) {
        this.jdbcname = jdbcname == null ? null : jdbcname.trim();
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