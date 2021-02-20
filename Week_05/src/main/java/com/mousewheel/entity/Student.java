package com.mousewheel.entity;

import lombok.Data;

@Data
public class Student {

    private String name;

    private Integer age;

    public Student(String name, Integer age){
        this.name = name;
        this.age = age;
    }

    public void sayHello() {
        System.out.println("Hello, I'm " + name);
    }
}
