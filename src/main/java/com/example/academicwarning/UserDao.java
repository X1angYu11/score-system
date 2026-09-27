package com.example.academicwarning;

public interface UserDao {
    SysUser findByUsername(String username);
    int insert(SysUser user);
}
