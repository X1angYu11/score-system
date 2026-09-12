package com.example.scoresystem;
import java.util.List;

public interface StudentService {
    List<Student> findAll();

    Student findById(String id);

    Student findByName(String name);

    int insert(Student stu);

    int updateScore(String id, double score);

    int deleteById(String id);

    List<Student> findAllByScoreDesc();
    int insertBatch(List<Student> students);
}
