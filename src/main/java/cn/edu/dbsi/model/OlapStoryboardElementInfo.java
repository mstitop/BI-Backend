package cn.edu.dbsi.model;

public class OlapStoryboardElementInfo {
    private Integer id;

    private Integer storyboardId;

    private String name;

    private String saikuId;

    private String saikuName;

    private String saikuPath;

    private Integer index;

    private Integer pointX;

    private Integer pointY;

    private Integer width;

    private Integer height;

    private String isDelete;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getStoryboardId() {
        return storyboardId;
    }

    public void setStoryboardId(Integer storyboardId) {
        this.storyboardId = storyboardId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name == null ? null : name.trim();
    }

    public String getSaikuId() {
        return saikuId;
    }

    public void setSaikuId(String saikuId) {
        this.saikuId = saikuId;
    }

    public String getSaikuName() {
        return saikuName;
    }

    public void setSaikuName(String saikuName) {
        this.saikuName = saikuName == null ? null : saikuName.trim();
    }

    public String getSaikuPath() {
        return saikuPath;
    }

    public void setSaikuPath(String saikuPath) {
        this.saikuPath = saikuPath == null ? null : saikuPath.trim();
    }

    public Integer getIndex() {
        return index;
    }

    public void setIndex(Integer index) {
        this.index = index;
    }

    public Integer getPointX() {
        return pointX;
    }

    public void setPointX(Integer pointX) {
        this.pointX = pointX;
    }

    public Integer getPointY() {
        return pointY;
    }

    public void setPointY(Integer pointY) {
        this.pointY = pointY;
    }

    public Integer getWidth() {
        return width;
    }

    public void setWidth(Integer width) {
        this.width = width;
    }

    public Integer getHeight() {
        return height;
    }

    public void setHeight(Integer height) {
        this.height = height;
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete == null ? null : isDelete.trim();
    }
}