package com.mousewheel.entity;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Data
@Component("studentByAnnotation")
public class StudentByAnnotation {
    @Value("qianliu")
    private String name;

    @Value("22")
    private Integer age;

    public void sayHello() {
        System.out.println("Hello, I'm " + name);
    }
}
