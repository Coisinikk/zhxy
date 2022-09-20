package com.zjy.zhxy.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.zjy.zhxy.pojo.Admin;
import com.zjy.zhxy.pojo.LoginForm;
import com.zjy.zhxy.pojo.Student;
import com.zjy.zhxy.pojo.Teacher;
import com.zjy.zhxy.service.AdminService;
import com.zjy.zhxy.service.StudentService;
import com.zjy.zhxy.service.TeacherService;
import com.zjy.zhxy.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

@Api(tags = "系统控制器")
@RestController//因为前后端异步交互，所以使用这个
@RequestMapping("/sms/system")
public class SystemController {
    @Autowired
    private AdminService adminService;
    @Autowired
    private TeacherService teacherService;
    @Autowired
    private StudentService studentService;

    //sms/system/updatePwd/123456/admin
    //sms/system/updatePwd/{oldPwd}/{newPwd}  请求参数有 oldPwd  newPwd  token
    @ApiOperation("更新用户密码")
    @PostMapping("/updatePwd/{oldPwd}/{newPwd}")
    public Result updatePwd(@ApiParam("token指令") @RequestHeader("token") String token,@ApiParam("旧密码") @PathVariable("oldPwd") String oldPwd,@ApiParam("新密码") @PathVariable("newPwd") String newPwd){
        boolean expiration = JwtHelper.isExpiration(token);
        if(expiration){
            //token过期
            return Result.fail().message("token失效，请重新登录后修改密码");
        }
        //获取用户ID和类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);
        oldPwd = MD5.encrypt(oldPwd);
        newPwd = MD5.encrypt(newPwd);
        switch (userType){
            case 1:
                QueryWrapper<Admin> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("id",userId.intValue());
                queryWrapper.eq("password",oldPwd);
                Admin admin = adminService.getOne(queryWrapper);
                if(admin != null){
                    admin.setPassword(newPwd);
                    adminService.saveOrUpdate(admin);
                }else {
                    return Result.fail().message("原密码有误!");
                }
                break;
            case 2:
                QueryWrapper<Student> queryWrapper1 = new QueryWrapper<>();
                queryWrapper1.eq("id",userId.intValue());
                queryWrapper1.eq("password",oldPwd);
                Student student = studentService.getOne(queryWrapper1);
                if(student != null){
                    student.setPassword(newPwd);
                    studentService.saveOrUpdate(student);
                }else {
                    return Result.fail().message("原密码有误!");
                }
                break;
            case 3:
                QueryWrapper<Teacher> queryWrapper2 = new QueryWrapper<>();
                queryWrapper2.eq("id",userId.intValue());
                queryWrapper2.eq("password",oldPwd);
                Teacher teacher = teacherService.getOne(queryWrapper2);
                if(teacher != null){
                    teacher.setPassword(newPwd);
                    teacherService.saveOrUpdate(teacher);
                }else {
                    return Result.fail().message("原密码有误!");
                }
                break;
        }
        return Result.ok();
    }



    //sms/system/headerImgUpload
    @ApiOperation("文件上传统一入口")
    @PostMapping("/headerImgUpload")
    public Result headerImgUpload(@ApiParam("上传的头像文件") @RequestPart("multipartFile") MultipartFile multipartFile,HttpServletRequest request){

        String uuid = UUID.randomUUID().toString().replace("-","").toLowerCase();
        String originalFilename = multipartFile.getOriginalFilename();
        int i = originalFilename.lastIndexOf(".");
        String newFileName = uuid.concat(originalFilename.substring(i));
        //保存文件,将文件发送到第三方/独立的图片服务器
        String portraitPath = "E:/ziy_code/zhxy/target/classes/public/upload/".concat(newFileName);
        try {
            multipartFile.transferTo(new File(portraitPath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        //响应图片路径
        String path = "upload/".concat(newFileName);
        return Result.ok(path);
    }



    @ApiOperation("通过token指令获取当前登录的用户信息的方法")
    @GetMapping("/getInfo")
    public Result getInfoByToken(@ApiParam("token指令") @RequestHeader("token") String token){
        //验证token是否过期
        boolean expiration = JwtHelper.isExpiration(token);
        if(expiration){
            return Result.build(null,ResultCodeEnum.TOKEN_ERROR);
        }
        //从token中解析用户id和用户类型
        Long userId = JwtHelper.getUserId(token);
        Integer userType = JwtHelper.getUserType(token);

        Map<String,Object> map = new LinkedHashMap<>();
        switch (userType){
            case 1:
                Admin admin = adminService.getAdminById(userId);
                map.put("userType",1);
                map.put("user",admin);
                break;
            case 2:
                Student student = studentService.getStudentById(userId);
                map.put("userType",2);
                map.put("user",student);
                break;
            case 3:
                Teacher teacher = teacherService.getTeacherById(userId);
                map.put("userType",3);
                map.put("user",teacher);
                break;
        }
        return Result.ok(map);
    }




    @ApiOperation("登录的方法")
    @PostMapping("/login")
    public Result login(@ApiParam("登录提交的form表单") @RequestBody LoginForm loginForm,HttpServletRequest request){
        //验证码校验
        HttpSession session = request.getSession();
        String sessionVerifiCode = (String)session.getAttribute("verifiCode");
        String loginVerifiCode = loginForm.getVerifiCode();
        if("".equals(sessionVerifiCode)||null==sessionVerifiCode){
            return Result.fail().message("验证码失效,请刷新后重试");
        }
        if(!sessionVerifiCode.equalsIgnoreCase(loginVerifiCode)){
            return Result.fail().message("验证码有误,请重新输入");
        }
        //从session中移除现有验证码
        session.removeAttribute("verifiCode");
        //分用户类型进行校验

        //准备一个map,存放用户响应的数据
        Map<String,Object> map = new LinkedHashMap<>();
        switch (loginForm.getUserType()){
            case 1:
                try {
                    Admin admin = adminService.login(loginForm);
                    if(null != admin){
                        String token = JwtHelper.createToken(admin.getId().longValue(),1);
                        map.put("token",token);
                    }else {
                        throw new RuntimeException("用户名或密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }

            case 2:
                try {
                    Student student = studentService.login(loginForm);
                    if(null != student){
                        String token = JwtHelper.createToken(student.getId().longValue(),2);
                        map.put("token",token);
                    }else {
                        throw new RuntimeException("用户名或密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
            case 3:
                try {
                    Teacher teacher = teacherService.login(loginForm);
                    if(null != teacher){
                        String token = JwtHelper.createToken(teacher.getId().longValue(),3);
                        map.put("token",token);
                    }else {
                        throw new RuntimeException("用户名或密码有误");
                    }
                    return Result.ok(map);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    return Result.fail().message(e.getMessage());
                }
        }
        return Result.fail().message("未找到该用户");
    }

    @ApiOperation("获取验证码图片")
    @GetMapping("/getVerifiCodeImage")
    public void getVerifiCodeImage(HttpServletRequest request, HttpServletResponse response){
        //获取图片
        BufferedImage verifiCodeImage = CreateVerifiCodeImage.getVerifiCodeImage();
        //获取图片上的验证码
        String verifiCode = new String(CreateVerifiCodeImage.getVerifiCode());
        //将验证码文本放入session域，为下一次验证做准备
        HttpSession session = request.getSession();
        session.setAttribute("verifiCode",verifiCode);
        //将验证码图片响应给浏览器
        try {
            ImageIO.write(verifiCodeImage,"JPEG",response.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
