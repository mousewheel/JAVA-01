package com.mousewheel.dao;

import com.mousewheel.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component("studentDao")
public class StudentDao {
    @Autowired
    JdbcTemplate jt;

    public List<Student> queryList(){
        List<Student> students = new ArrayList<>();

        String sql="select * from student";
        jt.query(sql,(rs) -> {
            Student student= new Student();
            student.setName(rs.getString("name"));
            student.setAge(rs.getInt("age"));
            students.add(student);
        });

        if (null != students && students.size()>0) {
            System.out.println("students list size " + students);
        } else {
            System.out.println("students list empty");
        }
        return new ArrayList<>();
    }
}
