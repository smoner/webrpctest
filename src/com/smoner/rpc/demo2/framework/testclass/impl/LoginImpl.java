package com.smoner.rpc.demo2.framework.testclass.impl;

import com.smoner.rpc.demo2.framework.testclass.itf.ILoginService;
import com.smoner.rpc.demo2.framework.testclass.itf.UserVO;

/**
 * Created by smoner on 2017/1/28.
 */
public class LoginImpl implements ILoginService{
    @Override
    public UserVO login(UserVO userVO) {
        UserVO userVO2 =new UserVO();
        userVO2.setName("admin2");
        userVO2.setPwd("admin22");
        return userVO2;
    }
}
