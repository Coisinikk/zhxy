package com.zjy.zhxy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjy.zhxy.pojo.Clazz;
import com.zjy.zhxy.service.ClazzService;
import com.zjy.zhxy.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Api(tags = "班级控制器")
@RestController//因为前后端异步交互，所以使用这个
@RequestMapping("/sms/clazzController")
public class ClazzController {
    @Autowired
    private ClazzService clazzService;

    //sms/gradeController/getClazzs
    @ApiOperation("获取全部的班级")
    @GetMapping("/getClazzs")
    public Result getClazzs(){
        List<Clazz> clazzs = clazzService.getClazzs();
        return Result.ok(clazzs);
    }



    //sms/clazzController/deleteClazz [1,2,3]
    @ApiOperation("删除Clazz信息")
    @DeleteMapping("/deleteClazz")
    public Result deleteGrade(@ApiParam("要删除的所有的clazz的id，JSON集合") @RequestBody List<Integer> ids){
        clazzService.removeByIds(ids);
        return Result.ok();
    }


    //sms/clazzController/saveOrUpdateClazz
    @ApiOperation("新增或修改clazz信息，有id则修改对象，没有则增加对象")
    @PostMapping("/saveOrUpdateClazz")
    public Result saveOrUpdateClazz(@ApiParam("JSON格式的Clazz对象") @RequestBody Clazz clazz){
        clazzService.saveOrUpdate(clazz);
        //接收参数
        //调用服务层方法完成增加或修改
        return Result.ok();
    }






    //sms/clazzController/getClazzsByOpr/1/3?gradeName=&name=
    @ApiOperation("分页带条件查询班级信息")
    @GetMapping("/getClazzsByOpr/{pageNo}/{pageSize}")
    public Result getClazzs(@ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo,@ApiParam("分页查询的页大小") @PathVariable("pageSize") Integer pageSize, @ApiParam("分页查询的查询条件") Clazz clazz){
        //分页  带条件查询
        Page<Clazz> page= new Page<>(pageNo,pageSize);
        //通过服务层
        IPage<Clazz> pageRs = clazzService.getClazzsByOpr(page,clazz);
        //封装Result对象并返回
        return Result.ok(pageRs);
    }

}
