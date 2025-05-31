package com.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import  com.itheima.reggie.common.R;
import com.itheima.reggie.constext.BaseContext;
import lombok.extern.slf4j.Slf4j;
import  org.springframework.util.AntPathMatcher;
import  javax.servlet.*;
import  javax.servlet.annotation.WebFilter;
import  javax.servlet.http.HttpServletRequest;
import  javax.servlet.http.HttpServletResponse;
import  java.io.IOException;
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
                         FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
//1.获取本次请求的URI
        String requestURI = request.getRequestURI();
//定义不需要处理的请求路径
        String[] urls = new String[]{
                "/employee/login",
                "/employee/logout",
                "/user/sendMsg",
                "/user/login",
                "/backend/**",
                "/front/**"
        };
//2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
//3.如果不需要处理，则直接放行
        if (check) {
            filterChain.doFilter(request, response);
            return;
        }
//4.判断登录状态，如果已登录，则直接放行
//        if (request.getSession().getAttribute("employee") != null || request.getSession().getAttribute("user") != null) {
//            filterChain.doFilter(request, response);
//            return;
//        }

//5.如果未登录则返回未登录结果，通过输出流方式向客户端页面响应数据
//        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
//        return;
        Object employee = request.getSession().getAttribute("employee");
        Object user = request.getSession().getAttribute("user");

        Long currentId = null;

        if (employee != null) {
            currentId = (Long) employee;
        } else if (user != null) {
            currentId = (Long) user;
        }

        if (currentId != null) {
            try {
                // 设置用户ID到线程上下文
                BaseContext.setCurrentId(currentId);
                log.info("当前用户id为:{}", currentId);
                // 继续执行后续逻辑
                filterChain.doFilter(request, response);
            } finally {
                // 请求结束后清理 ThreadLocal，防止内存泄漏
                BaseContext.removeCurrentId();
            }
        } else {
            // 未登录，返回错误
            response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        }
    }
    /**
     * 路径匹配，检查本次请求是否需要放行
     */
    public  boolean  check(String[] urls, String requestURI) {
        for  (String url : urls) {
            boolean  match = PATH_MATCHER.match(url, requestURI);
            if  (match) {
                return  true ;
            }
        }
        return  false ;
    }
}
