package cn.edu.dbsi.service.impl;

import cn.edu.dbsi.dao.UserMapper;
import cn.edu.dbsi.model.User;
import cn.edu.dbsi.service.LoginServiceI;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by 郭世明 on 2017/6/2.
 */
@Service("userService")
public class LoginServiceImpl implements LoginServiceI {

    @Autowired
    private UserMapper userMapper;

    public User getUserByUsernameAndPassword(String username,String password) {
        return userMapper.selectByPasswordAndUsername(username,password);

    }

    public User getUserByUserId(Integer id) {
        return userMapper.selectByPrimaryKey(id);
    }
}
