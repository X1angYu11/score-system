package com.example.academicwarning;

public interface UserService {
    boolean register(String username, String password);
    boolean checklogin(String username, String password);
}
