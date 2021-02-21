package com.mousewheel.entity;

import lombok.Data;

@Data
public class Student {

    private String name;

    private Integer age;


    public void sayHello() {
        System.out.println("Hello, I'm " + name);
    }
}
