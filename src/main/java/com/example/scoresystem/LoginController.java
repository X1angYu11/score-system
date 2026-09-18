package com.example.scoresystem;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
public class LoginController {
    private static final String USERNAME="zxy";
    private static final String PASSWORD="123456";
    @PostMapping("/login")
    public String login(@RequestBody Map<String,String> body,HttpSession session){
        String username=body.get("username");
        String password=body.get("password");
        if(!USERNAME.equals(username)){
            throw new UnauthorizedException("用户名错误");
        }else if(!PASSWORD.equals(password)){
            throw new UnauthorizedException("密码错误");
        }
        session.setAttribute("loginUser",username);
        return "登录成功"+username;
    }
}
