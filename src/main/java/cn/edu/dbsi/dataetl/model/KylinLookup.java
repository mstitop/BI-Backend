package cn.edu.dbsi.dataetl.model;

/**
 * Created by Skye on 2017/8/3.
 */
public class KylinLookup {
    private String name;
    private String primaryKey;
    private String foreignKey;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey) {
        this.primaryKey = primaryKey;
    }

    public String getForeignKey() {
        return foreignKey;
    }

    public void setForeignKey(String foreignKey) {
        this.foreignKey = foreignKey;
    }
}
