package com.example.academicwarning;
import java.util.List;
public interface CourseService {
    List<Course> findAll();
    Course findById(String courseId);
    int insert(Course course);
    int update(Course course);
    int deleteById(String courseId);
}
