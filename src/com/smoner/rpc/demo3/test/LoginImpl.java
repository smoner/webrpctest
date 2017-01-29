package com.smoner.rpc.demo3.test;

/**
 * Created by smoner on 2017/1/29.
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