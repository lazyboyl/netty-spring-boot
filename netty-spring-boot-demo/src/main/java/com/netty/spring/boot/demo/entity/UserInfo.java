package com.netty.spring.boot.demo.entity;

import java.util.Date;

/**
 * @author linzf
 * @since 2020/7/1
 * 类描述：
 */
public class UserInfo {

    private String name;

    private Integer age;

    private Date birth;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Date getBirth() {
        return birth;
    }

    public void setBirth(Date birth) {
        this.birth = birth;
    }
}
