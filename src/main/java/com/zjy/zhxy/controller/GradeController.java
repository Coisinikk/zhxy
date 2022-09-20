package com.zjy.zhxy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjy.zhxy.pojo.Grade;
import com.zjy.zhxy.service.GradeService;
import com.zjy.zhxy.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Api(tags = "年级控制器")
@RestController//因为前后端异步交互，所以使用这个
@RequestMapping("/sms/gradeController")
public class GradeController {
    @Autowired
    private GradeService gradeService;

    //sms/gradeController/getGrades
    @ApiOperation("获取全部的年级")
    @GetMapping("/getGrades")
    public Result getGrades(){
        List<Grade> grades = gradeService.getGrades();
        return Result.ok(grades);
    }



    //sms/gradeController/deleteGrade
    @ApiOperation("删除grade信息")
    @DeleteMapping("/deleteGrade")
    public Result deleteGrade(@ApiParam("要删除的所有的grade的id，JSON集合") @RequestBody List<Integer> ids){
        gradeService.removeByIds(ids);
        return Result.ok();
    }


    //sms/gradeController/saveOrUpdateGrade
    @ApiOperation("新增或修改Grade信息，有id则修改对象，没有则增加对象")
    @PostMapping("/saveOrUpdateGrade")
    public Result saveOrUpdateGrade(@ApiParam("JSON格式的Grade对象") @RequestBody Grade grade){
        gradeService.saveOrUpdate(grade);
        //接收参数
        //调用服务层方法完成增加或修改
        return Result.ok();
    }





    //sms/gradeController/getGrades/1/3?gradeName=%E%B8%89
    @ApiOperation("根据年纪名称模糊分页查询")
    @GetMapping("/getGrades/{pageNo}/{pageSize}")
    public Result getGrades(@ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo, @ApiParam("分页查询的页大小") @PathVariable("pageSize") Integer pageSize,@ApiParam("分页模糊查询的匹配名称") String gradeName){
        //分页  带条件查询
        Page<Grade> page= new Page<>(pageNo,pageSize);
        //通过服务层
        IPage<Grade> pageRs = gradeService.getGradeByOpr(page,gradeName);
        //封装Result对象并返回
        return Result.ok(pageRs);
    }

}
