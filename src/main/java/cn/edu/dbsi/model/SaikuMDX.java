package cn.edu.dbsi.model;

import java.util.Date;

public class SaikuMDX {
    private Integer id;

    private String mdx;

    private Date executeTime;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getMdx() {
        return mdx;
    }

    public void setMdx(String mdx) {
        this.mdx = mdx == null ? null : mdx.trim();
    }

    public Date getExecuteTime() {
        return executeTime;
    }

    public void setExecuteTime(Date executeTime) {
        this.executeTime = executeTime;
    }
}