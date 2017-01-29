package com.smoner.rpc.demo2.framework.testclass.itf;

import java.io.Serializable;

/**
 * Created by smoner on 2017/1/28.
 */
public class UserVO implements Serializable{

    private String name;
    private String pwd;
    private int age;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
