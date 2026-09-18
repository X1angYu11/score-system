package com.example.scoresystem;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class LoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler){
        HttpSession session=request.getSession(false);
        if(session==null||session.getAttribute("loginUser")==null){
            throw new UnauthorizedException("请先登录");
        }
        return true;
    }
}
