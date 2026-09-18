package com.example.scoresystem;
import java.util.Map;
import java.util.List;
public interface StudentDao {
    List<Student> findAll();

    Student findById(String id);

    Student findByName(String name);

    int insert(Student stu);

    int updateScore(String id, double score);

    int deleteById(String id);

    List<Student> findAllByScoreDesc();

    List<Student> findPage(int offset,int size);

    int count();

    Map<String,Object> stats();

    List<Student> findTop(int n);
}
