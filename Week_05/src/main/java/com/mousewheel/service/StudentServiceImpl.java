package com.mousewheel.service;

import com.mousewheel.dao.StudentDao;
import com.mousewheel.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {

    @Autowired
   private StudentDao studentDao;

    @Override
    public List<Student> queryList() {
        return studentDao.queryList();
    }

    @Override
    public void doHomeWork() {
        System.out.println("I'm doing homework!");
    }
}
