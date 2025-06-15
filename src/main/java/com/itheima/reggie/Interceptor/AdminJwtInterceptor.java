package com.itheima.reggie.Interceptor;


import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.R;
import com.itheima.reggie.constext.BaseContext;
import com.itheima.reggie.properties.JwtProperties;
import com.itheima.reggie.utils.JwtUtils;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class AdminJwtInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        //判断当前拦截的是controller的方法还是其他资源
        if(!(handler instanceof HandlerMethod)){
            //不是controller方法,直接放行
            return true;
        }

        //从session中获取令牌
        String token = (String) request.getSession().getAttribute("token");

        //判断令牌是否存在
        if (!StringUtils.hasLength(token)){
            log.info("用户未登录");
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return false;
        }

        try {
            //解析令牌
            Claims claims = JwtUtils.parseJWT(jwtProperties.getAdminSecretKey(), token);

            BaseContext.setCurrentId(Long.parseLong(claims.get("id").toString()));
            log.info("用户已登录，用户id为：{}",claims.get("id"));
            return true;

        } catch (Exception e) {
            log.info("用户登录失败");
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
            return false;
        }
    }

}
