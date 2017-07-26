package cn.edu.dbsi.service;

import cn.edu.dbsi.model.User;

/**
 * Created by 郭世明 on 2017/6/2.
 */
public interface LoginServiceI {
    User getUserByUsernameAndPassword(String username,String password);

    User getUserByUserId(Integer id);
}
