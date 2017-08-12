package cn.edu.dbsi.dataetl.model;

import java.util.List;

/**
 * Created by Skye on 2017/8/3.
 */
public class KylinTable {
    private String factTable;
    private List<KylinLookup> kylinLookups;

    public String getFactTable() {
        return factTable;
    }

    public void setFactTable(String factTable) {
        this.factTable = factTable;
    }

    public List<KylinLookup> getKylinLookups() {
        return kylinLookups;
    }

    public void setKylinLookups(List<KylinLookup> kylinLookups) {
        this.kylinLookups = kylinLookups;
    }
}
