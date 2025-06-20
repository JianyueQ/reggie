package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.itheima.reggie.common.R;
import com.itheima.reggie.constext.BaseContext;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.properties.JwtProperties;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.JwtUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/sendMsg")
    public R<Map<String, String>> sendMsg(@RequestBody User user, HttpSession session) {
        //获取手机号
        String phone = user.getPhone();
        if(StringUtils.isNotEmpty(phone)) {
            //生成随机的4位验证码
//            String code = String.format("%06d", (int) (Math.random() * 10000));
            String code = String.valueOf(ValidateCodeUtils.generateValidateCode(6));
            log.info("生成的验证码 (控制台输出): {}", code);
            //调用阿里云提供的短信服务API完成发送短信 (此处为模拟)


            //需要将生成的验证码保存到Session中
            session.setAttribute(phone, code);

            Map<String, String> codeMap = new HashMap<>();
            codeMap.put("phone", phone);
            codeMap.put("code", code);

            return R.success(codeMap);
        }
        return R.error("短信发送失败");
    }
    @PostMapping("/login")
    public R<User> login(@RequestBody Map map, HttpSession session) {
        log.info(map.toString());

        //获取手机号
        String phone = map.get("phone").toString();

        //获取验证码
        String code = map.get("code").toString();

        //从Session中获取保存的验证码
        Object codeInSession = session.getAttribute(phone);

        //进行验证码的比对（页面提交的验证码和Session中保存的验证码比对）
        if(codeInSession != null && codeInSession.equals(code)) {
            //如果能够比对成功，说明登录成功
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone, phone);

            User user = userService.getOne(queryWrapper);

            if(user == null) {
                //判断当前手机号对应的用户是否为新用户，如果是新用户则自动完成注册
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                //生成一串随机英文加数字的名字
                user.setName(String.valueOf(Math.random()).substring(2, 6));
                userService.save(user);
            }

            Map<String, Object> claims = new HashMap<>();
            claims.put("id", user.getId());
            claims.put("phone", phone);
            String token = JwtUtils.createJWT(jwtProperties.getUserSecretKey(), jwtProperties.getUserTtl(), claims);
            session.setAttribute("user", user.getId());
            session.setAttribute("token", token);
            return R.success(user);
        }
        return R.error("登录失败");
    }

    @PostMapping("/loginout")
    public R<String> logout(HttpSession session) {
        session.removeAttribute("user");
        BaseContext.removeCurrentId();
        return R.success("退出成功");
    }

}
