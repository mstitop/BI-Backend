package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.HiveTableInfoMapper;
import cn.edu.dbsi.model.HiveTableInfo;
import cn.edu.dbsi.service.HiveTableInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by Skye on 2017/7/10.
 */
@Service("hiveTableInfoService")
public class HiveTableInfoServiceImp implements HiveTableInfoService {
    @Autowired
    HiveTableInfoMapper hiveTableInfoMapper;

    public int addHiveTableInfo(HiveTableInfo hiveTableInfo) {
        return hiveTableInfoMapper.insert(hiveTableInfo);
    }

    public List<String> selectTableNameBytask(int taskId) {
        return hiveTableInfoMapper.selectTableNameBytask(taskId);
    }
}
