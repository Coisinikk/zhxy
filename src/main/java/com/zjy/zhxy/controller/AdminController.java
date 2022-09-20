package com.zjy.zhxy.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.zjy.zhxy.pojo.Admin;
import com.zjy.zhxy.service.AdminService;
import com.zjy.zhxy.util.MD5;
import com.zjy.zhxy.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
@Api(tags = "管理员控制器")
@RestController//因为前后端异步交互，所以使用这个
@RequestMapping("/sms/adminController")
public class AdminController {

    @Autowired
    private AdminService adminService;
    //sms/adminController/getAllAdmin/1/3?adminName=a
    @ApiOperation("分页带条件查询管理员信息")
    @GetMapping("/getAllAdmin/{pageNo}/{pageSize}")
    public Result getAdmins(@ApiParam("分页查询的页码数") @PathVariable("pageNo") Integer pageNo, @ApiParam("分页查询的页大小") @PathVariable("pageSize") Integer pageSize, @ApiParam("分页查询的查询条件") String adminName){
        //分页  带条件查询
        Page<Admin> page= new Page<>(pageNo,pageSize);
        //通过服务层
        IPage<Admin> pageRs = adminService.getAdminsByOpr(page,adminName);
        //封装Result对象并返回
        return Result.ok(pageRs);
    }

    //sms/adminController/saveOrUpdateAdmin
    @ApiOperation("新增或修改admin信息，有id则修改对象，没有则增加对象")
    @PostMapping("/saveOrUpdateAdmin")
    public Result saveOrUpdateAdmin(@ApiParam("JSON格式的Admin对象") @RequestBody Admin admin){

        Integer id = admin.getId();
        if(null==id || 0==id){
            admin.setPassword(MD5.encrypt(admin.getPassword()));
        }
        adminService.saveOrUpdate(admin);
        //接收参数
        //调用服务层方法完成增加或修改
        return Result.ok();
    }

    //sms/adminController/deleteAdmin
    @ApiOperation("删除admin信息")
    @DeleteMapping("/deleteAdmin")
    public Result deleteGrade(@ApiParam("要删除的所有的admin的id，JSON集合") @RequestBody List<Integer> ids){
        adminService.removeByIds(ids);
        return Result.ok();
    }

}
