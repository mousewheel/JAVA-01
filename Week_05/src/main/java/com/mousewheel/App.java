package com.mousewheel;

import com.mousewheel.dao.StudentDao;
import com.mousewheel.entity.Student;
import com.mousewheel.entity.StudentByAnnotation;
import com.mousewheel.entity.StudentByClass;
import com.mousewheel.service.StudentService;
import com.mousewheel.service.StudentServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Proxy;

@Component
public class App {

    @Autowired
    private StudentService studentService1;

    public static void main( String[] args ) {
        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
        Student studentById = (Student)applicationContext.getBean("studentById");
        studentById.sayHello();

        Student studentByName = (Student)applicationContext.getBean("studentByName");
        studentByName.sayHello();

        StudentByClass studentByClass = applicationContext.getBean(StudentByClass.class);
        studentByClass.sayHello();

        StudentByAnnotation studentByAnnotation = (StudentByAnnotation)applicationContext.getBean("studentByAnnotation");
        studentByAnnotation.sayHello();

        StudentDao studentDao = (StudentDao) applicationContext.getBean("studentDao");
        studentDao.queryList();
        StudentServiceImpl studentService = new StudentServiceImpl();
        StudentService proxyObj = (StudentService) Proxy.newProxyInstance(StudentService.class.getClassLoader(),
                new Class[]{StudentService.class}, (proxy, method, a) -> {
            Object obj = method.invoke(studentService, a);
            return obj;
        });
        proxyObj.doHomeWork();
    }
}
