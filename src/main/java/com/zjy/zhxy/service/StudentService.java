package com.zjy.zhxy.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.zjy.zhxy.pojo.Admin;
import com.zjy.zhxy.pojo.Clazz;
import com.zjy.zhxy.pojo.LoginForm;
import com.zjy.zhxy.pojo.Student;

public interface StudentService extends IService<Student> {

    Student getStudentById(Long userId);

    Student login(LoginForm loginForm);

    IPage<Student> getStudentByOpr(Page<Student> page, Student student);
}
