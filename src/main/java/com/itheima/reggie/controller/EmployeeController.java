package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.entity.vo.EmployeeVo;
import com.itheima.reggie.properties.JwtProperties;
import com.itheima.reggie.service.EmployeeService;
import com.itheima.reggie.utils.JwtUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request,@RequestBody Employee employee){
        //1.将页面提交的密码password进行MD5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2.根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = employeeService.getOne(queryWrapper);
        //3.如果没有查询到员工信息或者密码不正确，则返回登录失败结果
        if(emp == null || !emp.getPassword().equals(password)){
            return R.error("用户名或密码错误！");
        }
        //4.查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if(emp.getStatus() == 0){
            return R.error("账号已禁用");
        }
        //5.登录成功，将员工Id存入Session并返回员工信息
        request.getSession().setAttribute("employee",emp.getId());

        return R.success(emp);
    }

//    @PostMapping ("/login")
//    public R<EmployeeVo> login(@RequestBody Employee employee){
//
//        //将密码进行Md5加密
//        String  password = employee.getPassword();
//        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        //查询数据库，根据用户名查询员工数据
//        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
//        queryWrapper.eq(Employee::getUsername,employee.getUsername());
//        Employee emp = employeeService.getOne(queryWrapper);
//        if ( emp == null ||  !emp.getPassword().equals(password)){
//            return R.error("用户名或密码错误");
//        }
//         if (emp.getStatus() == 0){
//            return R.error("账号已禁用");
//        }
//        //登录成功,生成jwt令牌
//        Map<String,Object> claims = new HashMap<>();
//
//        claims.put("id",emp.getId());
//        String token = JwtUtils.createJWT(
//                jwtProperties.getAdminSecretKey(),
//                jwtProperties.getAdminTtl(),
//                claims);
//        EmployeeVo employeeVo = new EmployeeVo();
//        BeanUtils.copyProperties( emp,employeeVo);
//        employeeVo.setToken(token);
//        log.info("token = {}",token);
//        return R.success(employeeVo);
//
//    }

    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        //清除Session中保存的当前登录员工的id
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

}