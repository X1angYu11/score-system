package com.example.academicwarning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;
@Repository
public class UserDaoImpl implements UserDao {
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public SysUser findByUsername(String username) {
        List<SysUser> list= jdbcTemplate.query("select id,username,password from sys_user where username=?",(rs,rowNum)->new SysUser(rs.getInt("id"),rs.getString("username"),rs.getString("password")),username);
        return list.isEmpty()?null:list.get(0);

    }
    @Override
    public int insert(SysUser user) {
        return jdbcTemplate.update("insert into sys_user(username,password) values(?,?)",user.getUsername(),user.getPassword());
    }
}
