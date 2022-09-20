package com.zjy.zhxy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.zjy.zhxy.mapper.StudentMapper;
import com.zjy.zhxy.pojo.LoginForm;
import com.zjy.zhxy.pojo.Student;
import com.zjy.zhxy.service.StudentService;
import com.zjy.zhxy.util.MD5;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

@Service("studentServiceImpl")
@Transactional
public class StudentServiceImpl extends ServiceImpl<StudentMapper, Student> implements StudentService {
    @Override
    public Student getStudentById(Long userId) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id",userId);
        Student student = baseMapper.selectOne(queryWrapper);
        return student;
    }

    @Override
    public Student login(LoginForm loginForm) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("name",loginForm.getUsername());
        queryWrapper.eq("password", MD5.encrypt(loginForm.getPassword()));
        Student student = baseMapper.selectOne(queryWrapper);
        return student;
    }

    @Override
    public IPage<Student> getStudentByOpr(Page<Student> pageParam, Student student) {
        QueryWrapper<Student> queryWrapper = new QueryWrapper<>();

        if(!StringUtils.isEmpty(student.getName())){
            queryWrapper.like("name",student.getName());
        }

        if(!StringUtils.isEmpty(student.getClazzName())){
            queryWrapper.like("clazz_name",student.getClazzName());
        }
        queryWrapper.orderByAsc("id");
        Page<Student> page = baseMapper.selectPage(pageParam,queryWrapper);
        return page;
    }
}
