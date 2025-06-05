package com.itheima.reggie.entity.vo;

import com.itheima.reggie.entity.Employee;
import lombok.Data;

@Data
public class EmployeeVo extends Employee {
    private String token;
}
