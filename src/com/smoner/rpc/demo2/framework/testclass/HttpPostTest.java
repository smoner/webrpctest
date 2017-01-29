package com.smoner.rpc.demo2.framework.testclass;

import com.smoner.rpc.demo2.framework.testclass.itf.ILoginService;
import com.smoner.rpc.demo2.framework.testclass.itf.UserVO;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by smoner on 2017/1/28.
 */
public class HttpPostTest {
    public static void main(String[] args) {
        String path = "http://127.0.0.1:18080/webrpctest/servlet/LoginAction";
        ILoginService loginService =  new NCLocator().lookup(ILoginService.class);
        UserVO userVO = new UserVO();
        userVO.setName("admin");
        userVO.setPwd("admin");
        UserVO userVO2 = loginService.login(userVO);
        String dd = null;
    }
}
