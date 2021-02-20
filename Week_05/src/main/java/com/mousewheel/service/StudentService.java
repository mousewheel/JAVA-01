package com.mousewheel.service;

import com.mousewheel.entity.Student;

import java.util.List;

public interface StudentService {

    List<Student> queryList();

    void doHomeWork();

}
