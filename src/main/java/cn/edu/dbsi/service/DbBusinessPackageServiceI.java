package cn.edu.dbsi.service;

import cn.edu.dbsi.model.DbBusinessPackage;

import java.util.List;

/**
 * Created by 郭世明 on 2017/6/26.
 */
public interface DbBusinessPackageServiceI {

    int addDbBusinessPackage(DbBusinessPackage dbBusinessPackage);

    int updateDbBusinessPackage(DbBusinessPackage dbBusinessPackage);

    int selectDbBusinessPackagePrimaryKey(DbBusinessPackage dbBusinessPackage);

    List<DbBusinessPackage> getDbBusinessPackagesByBpid(Integer bpid);
}
