package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.DbconnInfoMapper;
import cn.edu.dbsi.model.DbconnInfo;
import cn.edu.dbsi.service.DbConnectionServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by 郭世明 on 2017/6/22.
 */
@Service("dbConnectionService")
public class DbConnectionServiceImpl implements DbConnectionServiceI {
    @Autowired
    private DbconnInfoMapper dbconnInfoMapper;

    public int deleteDbConnInfo(Integer id) {
        return dbconnInfoMapper.updateIsDeleteByPrimaryKey(id);
    }

    public int updateDbConnInfo(DbconnInfo dbconnInfo) {
        return dbconnInfoMapper.updateByPrimaryKeySelective(dbconnInfo);
    }

    public int addDbConnInfo(DbconnInfo dbconnInfo) {
        return dbconnInfoMapper.insert(dbconnInfo);
    }

    public DbconnInfo getDbconnInfoById(Integer id) {
        return dbconnInfoMapper.selectByPrimaryKey(id);
    }

    public List<DbconnInfo> getDbConnInfo() {
            return dbconnInfoMapper.selectAll();
    }

}
