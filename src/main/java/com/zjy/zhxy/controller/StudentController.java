package com.zjy.zhxy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjy.zhxy.pojo.Clazz;
import com.zjy.zhxy.pojo.Student;
import com.zjy.zhxy.service.StudentService;
import com.zjy.zhxy.util.MD5;
import com.zjy.zhxy.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "学生控制器")
@RestController//因为前后端异步交互，所以使用这个
@RequestMapping("/sms/studentController")
public class StudentController {
    @Autowired
    private StudentService studentService;

    //sms/studentController/delStudentById
    @ApiOperation("删除student信息")
    @DeleteMapping("/delStudentById")
    public Result deleteGrade(@ApiParam("要删除的所有的student的id，JSON集合") @RequestBody List<Integer> ids){
        studentService.removeByIds(ids);
        return Result.ok();
    }




    //sms/studentController/addOrUpdateStudent
    @ApiOperation("新增或修改student信息，有id则修改对象，没有则增加对象")
    @PostMapping("/addOrUpdateStudent")
    public Result addOrUpdateStudent(@ApiParam("JSON格式的Student对象") @RequestBody Student student){
        //是否需要密码转换
        Integer id = student.getId();
        if(null==id || 0==id){
            student.setPassword(MD5.encrypt(student.getPassword()));
        }
        studentService.saveOrUpdate(student);
        return Result.ok();
    }





    //sms/studentController/getStudentByOpr/1/3?name=&clazzName=
    @ApiOperation("分页带条件查询学生信息")
    @GetMapping("/getStudentByOpr/{pageNo}/{pageSize}")
    public Result getStudents(@ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo, @ApiParam("分页查询的页大小") @PathVariable("pageSize") Integer pageSize, @ApiParam("分页查询的查询条件") Student student){
        //分页  带条件查询
        Page<Student> page= new Page<>(pageNo,pageSize);
        //通过服务层
        IPage<Student> pageRs = studentService.getStudentByOpr(page,student);
        //封装Result对象并返回
        return Result.ok(pageRs);
    }

}
