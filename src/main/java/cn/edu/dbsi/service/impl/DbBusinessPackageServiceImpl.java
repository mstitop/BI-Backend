package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.DbBusinessPackageMapper;
import cn.edu.dbsi.model.DbBusinessPackage;
import cn.edu.dbsi.service.DbBusinessPackageServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 郭世明 on 2017/6/26.
 */

@Service("dbBusinessPackageService")
public class DbBusinessPackageServiceImpl implements DbBusinessPackageServiceI {

    @Autowired
    private DbBusinessPackageMapper dbBusinessPackageMapper;

    public int addDbBusinessPackage(DbBusinessPackage dbBusinessPackage) {
        return dbBusinessPackageMapper.insert(dbBusinessPackage);
    }

    public int updateDbBusinessPackage(DbBusinessPackage dbBusinessPackage) {
        return dbBusinessPackageMapper.updateByPrimaryKeySelective(dbBusinessPackage);
    }

    public int selectDbBusinessPackagePrimaryKey(DbBusinessPackage dbBusinessPackage) {
        return dbBusinessPackageMapper.selectDbBusinessPackagePrimaryKey(dbBusinessPackage);
    }

    public List<DbBusinessPackage> getDbBusinessPackagesByBpid(Integer bpid) {
        return dbBusinessPackageMapper.selectByBpid(bpid);
    }
}
