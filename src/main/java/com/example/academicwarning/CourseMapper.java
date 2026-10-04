package com.example.academicwarning;
import org.apache.ibatis.annotations.*;
import java.util.List;
@Mapper
public interface CourseMapper {
    @Select("select*from course")
    List<Course> findAll();
    @Select("select * from course where course_id = #{courseId}")
    Course findById(@Param("courseId") String courseId);
    @Insert("insert into course (course_id, course_name, credit, teacher) values (#{courseId}, #{courseName}, #{credit}, #{teacher})")
    int insert(Course course);
    @Update("update course set course_name = #{courseName}, credit = #{credit}, teacher = #{teacher} where course_id = #{courseId}")
    int update(Course course);
    @Delete("delete from course where course_id = #{courseId}")
    int deleteById(@Param("courseId") String courseId);
}
