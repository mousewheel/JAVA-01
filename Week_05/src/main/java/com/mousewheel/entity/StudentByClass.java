package com.mousewheel.entity;

import lombok.Data;

@Data
public class StudentByClass {
    private String name;

    private Integer age;

    public void sayHello() {
        System.out.println("Hello, I'm " + name);
    }
}
