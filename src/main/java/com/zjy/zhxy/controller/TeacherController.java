package com.zjy.zhxy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjy.zhxy.pojo.Student;
import com.zjy.zhxy.pojo.Teacher;
import com.zjy.zhxy.service.TeacherService;
import com.zjy.zhxy.util.MD5;
import com.zjy.zhxy.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Api(tags = "教师控制器")
@RestController//因为前后端异步交互，所以使用这个
@RequestMapping("/sms/teacherController")
public class TeacherController {
    @Autowired
    private TeacherService teacherService;
    //sms/teacherController/getTeachers/1/3?name=&clazzName=
    @ApiOperation("分页带条件查询教师信息")
    @GetMapping("/getTeachers/{pageNo}/{pageSize}")
    public Result getTeachers(@ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo, @ApiParam("分页查询的页大小") @PathVariable("pageSize") Integer pageSize, @ApiParam("分页查询的查询条件") Teacher teacher){
        //分页  带条件查询
        Page<Teacher> page= new Page<>(pageNo,pageSize);
        //通过服务层
        IPage<Teacher> pageRs = teacherService.getTeacherByOpr(page,teacher);
        //封装Result对象并返回
        return Result.ok(pageRs);
    }


    //sms/teacherController/saveOrUpdateTeacher
    @ApiOperation("新增或修改teacher信息，有id则修改对象，没有则增加对象")
    @PostMapping("/saveOrUpdateTeacher")
    public Result saveOrUpdateTeacher(@ApiParam("JSON格式的Teacher对象") @RequestBody Teacher teacher){
        //是否需要密码转换
        Integer id = teacher.getId();
        if(null==id || 0==id){
            teacher.setPassword(MD5.encrypt(teacher.getPassword()));
        }
        teacherService.saveOrUpdate(teacher);
        return Result.ok();
    }

    //sms/teacherController/deleteTeacher
    @ApiOperation("删除teacher信息")
    @DeleteMapping("/delStudentById")
    public Result deleteGrade(@ApiParam("要删除的所有的student的id，JSON集合") @RequestBody List<Integer> ids){
        teacherService.removeByIds(ids);
        return Result.ok();
    }
}
