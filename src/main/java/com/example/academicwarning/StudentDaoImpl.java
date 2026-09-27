package com.example.academicwarning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Map;

@Repository
public class StudentDaoImpl implements StudentDao{
    @Autowired
    private JdbcTemplate jdbcTemplate;
    @Override
    public List<Student> findAll(){
        return jdbcTemplate.query("select id,name,score from student",
                (rs, rowNum) -> new Student(rs.getString("id"), rs.getString("name"),rs.getDouble("score") ));
    }
    @Override
    public Student findById(String id){
        List<Student> list= jdbcTemplate.query("select id,name,score from student where id=?",
                (rs,rowNum)->new Student(rs.getString("id"),rs.getString("name"),rs.getDouble("score")),id);
        return list.isEmpty()? null:list.get(0);
    }
    @Override
    public Student findByName(String name){
        List<Student> list=jdbcTemplate.query("select id,name,score from student where name=?",
                (rs,rowNum)->new Student(rs.getString("id"),rs.getString("name"),rs.getDouble("score")),name);
        return list.isEmpty()? null:list.get(0);
    }
    @Override
    public int insert(Student stu){
        return jdbcTemplate.update("insert into student (id,name,score) values (?,?,?)",stu.getId(),stu.getName(),stu.getScore());
    }
    @Override
    public int updateScore(String id, double score) {
        return jdbcTemplate.update(
                "UPDATE student SET score = ? WHERE id = ?", score, id);
    }

    @Override
    public int deleteById(String id) {
        return jdbcTemplate.update("DELETE FROM student WHERE id = ?", id);
    }

    @Override
    public List<Student> findAllByScoreDesc() {
        return jdbcTemplate.query("SELECT id, name, score FROM student ORDER BY score DESC",
                (rs, rowNum) -> new Student(
                        rs.getString("id"),
                        rs.getString("name"),
                        rs.getDouble("score")));
    }
    @Override
    public List<Student> findPage(int offset,int size){
        return jdbcTemplate.query("select id,name,score from student order by id limit ?,?",
                (rs,rowNum)->new Student(rs.getString("id"),rs.getString("name"),
                        rs.getDouble("score")),offset,size);
    }
    @Override
    public int count(){
        Integer total=jdbcTemplate.queryForObject("select count(*) from student",Integer.class);
        return total==null? 0:total;
    }
    @Override
    public Map<String,Object> stats(){
        return jdbcTemplate.queryForMap("select count(*) as total,"+"AVG(score) as avgScore,"+"MAX(score) as maxScore,"+
                "MIN(score) as minScore,"+"SUM(case when score>=60 then 1 else 0 end) as passCount"+" from student");
    }
    @Override
    public List<Student> findTop(int n){
        String sql= """
                select id,name,score from student order by score desc limit ?""";
        return jdbcTemplate.query(sql,(rs,rowNum)->new Student(rs.getString("id"),
                rs.getString("name"),rs.getDouble("score")),n);
    }
}
