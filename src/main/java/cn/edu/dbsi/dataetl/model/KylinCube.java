package cn.edu.dbsi.dataetl.model;

import java.util.List;

/**
 * Created by Skye on 2017/8/3.
 */
public class KylinCube {
    private String name;
    private Integer taskId;
    private String description;
    private KylinTable kylinTables;
    private List<KylinDimension> kylinDimensionsList;
    private List<KylinMeasure> kylinMeasuresList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public KylinTable getKylinTables() {
        return kylinTables;
    }

    public void setKylinTables(KylinTable kylinTables) {
        this.kylinTables = kylinTables;
    }

    public Integer getTaskId() {
        return taskId;
    }

    public void setTaskId(Integer taskId) {
        this.taskId = taskId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }



    public List<KylinDimension> getKylinDimensionsList() {
        return kylinDimensionsList;
    }

    public void setKylinDimensionsList(List<KylinDimension> kylinDimensionsList) {
        this.kylinDimensionsList = kylinDimensionsList;
    }

    public List<KylinMeasure> getKylinMeasuresList() {
        return kylinMeasuresList;
    }

    public void setKylinMeasuresList(List<KylinMeasure> kylinMeasuresList) {
        this.kylinMeasuresList = kylinMeasuresList;
    }
}
