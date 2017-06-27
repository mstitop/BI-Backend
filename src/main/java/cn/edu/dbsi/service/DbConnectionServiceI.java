package cn.edu.dbsi.service;

import cn.edu.dbsi.model.DbconnInfo;

import java.util.List;

/**
 * Created by 郭世明 on 2017/6/22.
 */
public interface DbConnectionServiceI {

    List<DbconnInfo> getDbConnInfo();

    int addDbConnInfo(DbconnInfo dbconnInfo);

    int updateDbConnInfo(DbconnInfo dbconnInfo);

    int deleteDbConnInfo(Integer id);

    DbconnInfo getDbconnInfoById(Integer id);


}
