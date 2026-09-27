package com.example.academicwarning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserDao userDao;

    private final BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
    @Override
    public boolean register(String username,String password){
        if(userDao.findByUsername(username)!=null){
            return false;
        }
        String hash=encoder.encode(password);
        return userDao.insert(new SysUser(0,username,hash))>0;
    }
    @Override
    public boolean checklogin(String username,String password){
        SysUser user=userDao.findByUsername(username);
        if(user==null){
            return false;
        }
        return encoder.matches(password,user.getPassword());
    }
}
