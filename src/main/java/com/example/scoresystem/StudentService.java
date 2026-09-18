package com.example.scoresystem;
import java.util.List;
import java.util.Map;

public interface StudentService {
    List<Student> findAll();

    Student findById(String id);

    Student findByName(String name);

    int insert(Student stu);

    int updateScore(String id, double score);

    int deleteById(String id);

    List<Student> findAllByScoreDesc();
    int insertBatch(List<Student> students);
    List<Student> findPage(int page, int size);

    int count();
    Map<String, Object> stats();

    List<Map<String, Object>> findTop(int n);
}
