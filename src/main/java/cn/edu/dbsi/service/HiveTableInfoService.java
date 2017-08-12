package cn.edu.dbsi.service;

import cn.edu.dbsi.model.HiveTableInfo;

import java.util.List;

/**
 * Created by Skye on 2017/7/10.
 */
public interface HiveTableInfoService {
    int addHiveTableInfo(HiveTableInfo hiveTableInfo);
    List<String> selectTableNameBytask(int taskId);
}
