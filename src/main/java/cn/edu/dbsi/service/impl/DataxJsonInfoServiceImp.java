package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.DataxJsonInfoMapper;
import cn.edu.dbsi.model.DataxJsonInfo;
import cn.edu.dbsi.service.DataxJsonInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by Skye on 2017/7/10.
 */
@Service("dataxJsonInfoService")
public class DataxJsonInfoServiceImp implements DataxJsonInfoService{
    @Autowired
    private DataxJsonInfoMapper dataxJsonInfoMapper;
    public int addJsonInfo(DataxJsonInfo jsonInfo) {
        return dataxJsonInfoMapper.insert(jsonInfo);
    }
}
