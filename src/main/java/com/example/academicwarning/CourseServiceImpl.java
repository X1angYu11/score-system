package com.example.academicwarning;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseServiceImpl implements CourseService {
    @Autowired
    private CourseMapper courseMapper;
    @Override
    public List<Course> findAll() {
        return courseMapper.findAll();
    }
    @Override
    public Course findById(String courseId) {
        Course s= courseMapper.findById(courseId);
        if (s==null){
            throw new CourseNotFoundException("课程" + courseId + "不存在");
        }
        return s;
    }
    @Override
    public int insert(Course course) {
        return courseMapper.insert(course);
    }
    @Override
    public int update(Course course) {
        int i= courseMapper.update(course);
        if (i==0){
            throw new CourseNotFoundException("课程" + course.getCourseId() + "不存在，修改失败");
        }
        return i;
    }
    @Override
    public int deleteById(String courseId) {
        int i= courseMapper.deleteById(courseId);
        if (i==0){
            throw new CourseNotFoundException("课程" + courseId + "删除失败");
        }
        return i;
    }
}
