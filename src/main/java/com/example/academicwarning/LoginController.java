package com.example.academicwarning;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
public class LoginController {
    @Autowired
    private UserServiceImpl userService;
    private static final String USERNAME="zxy";
    private static final String PASSWORD="123456";
    @PostMapping("/register")
    public String register(@RequestBody Map<String,String> body,HttpSession session){
        boolean ok= userService.register(body.get("username"),body.get("password"));
        if(!ok){
            return "注册失败：用户名已存在";
        }
        session.setAttribute("loginUser",body.get("username"));

        return "注册成功"+body.get("username");
    }
    @PostMapping("/login")
    public String login(@RequestBody Map<String,String> body,HttpSession session){
        String username=body.get("username");
        String password=body.get("password");
        if(!userService.checklogin(username,password)){
            return "登录失败：用户名或密码错误";
        }
        session.setAttribute("loginUser",username);
        return "登录成功"+username;
    }
}
