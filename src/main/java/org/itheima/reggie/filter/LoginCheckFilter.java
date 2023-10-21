package org.itheima.reggie.filter;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.itheima.reggie.common.BaseContext;
import org.itheima.reggie.common.R;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 检查用户是否已经完成登录
 */

@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
@Slf4j
public class LoginCheckFilter implements Filter {
    //路径匹配器，支持通配符写法
    public static final AntPathMatcher PATH_MATCHER =new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response=(HttpServletResponse)servletResponse;
        //1.获取本次请求的URI
        String requestURI = request.getRequestURI();
        log.info("--------------------------------------拦截到请求{}---------------------------------------",requestURI);
        String[] urls =new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/common/**",
                "/user/sendMsg",
                "/user/login"
        };
        //2.判断本次请求是否需要处理
        boolean check = check(urls, requestURI);
        if (check){
            log.info("本次请求{}不需要处理",requestURI);
            filterChain.doFilter(request,response);
            return;
        }
        //4-1.判断登录状态，如果已经登录，则直接放行
        if (request.getSession().getAttribute("employee")!=null){
            log.info("用户已经登录，id为{}",request.getSession().getAttribute("employee"));

            BaseContext.setCurrentId((Long) request.getSession().getAttribute("employee"));
            //todo 获得线程id
            long id = Thread.currentThread().getId();
            log.info("thread id =>"+id);

            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
        //4-2.
        if (request.getSession().getAttribute("user")!=null){
            log.info("用户已经登录，id为{}",request.getSession().getAttribute("user"));

            BaseContext.setCurrentId((Long) request.getSession().getAttribute("user"));
            //todo 获得线程id
            long id = Thread.currentThread().getId();
            log.info("thread id =>"+id);

            filterChain.doFilter(request,response);
            return;
        }
        log.info("用户未登录");
        //5.如果未登录则返回未登录结果，通过输出流方式向客户端响应数据
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    /**
     * 路径匹配，检查本次请求是否需要放行
     * @param urls
     * @param requestURI
     * @return
     */
    public boolean check(String[] urls,String requestURI){
        for (String url:urls){
            boolean match = PATH_MATCHER.match(url, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
