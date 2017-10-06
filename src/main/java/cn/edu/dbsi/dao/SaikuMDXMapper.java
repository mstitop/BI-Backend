package cn.edu.dbsi.dao;

import cn.edu.dbsi.model.SaikuMDX;
import org.springframework.stereotype.Repository;

@Repository("SaikuMDXMapper")
public interface SaikuMDXMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(SaikuMDX record);

    int insertSelective(SaikuMDX record);

    SaikuMDX selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(SaikuMDX record);

    int updateByPrimaryKey(SaikuMDX record);

    int selectSaikuMdxLastPrimaryKey();
}