package com.smoner.rpc.demo3.myframework.client;

import com.smoner.rpc.demo3.test.ILoginService;
import com.smoner.rpc.demo3.test.UserVO;

public class HttpPostTest_Demo3 {
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
