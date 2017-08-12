package cn.edu.dbsi.dataetl.model;

/**
 * Created by Skye on 2017/8/3.
 */
public class KylinDimension {
    private String name;
    private String tableName;
    private String keyAttribute;
    private String columns;  // column 只有两个字段，不建model，使用字符串拼接 name1-alias2,name2-alias2....



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getKeyAttribute() {
        return keyAttribute;
    }

    public void setKeyAttribute(String keyAttribute) {
        this.keyAttribute = keyAttribute;
    }

    public String getColumns() {
        return columns;
    }

    public void setColumns(String columns) {
        this.columns = columns;
    }
}
