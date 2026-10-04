package com.example.academicwarning;


import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class Course {
    @NotBlank(message = "课程ID不能为空")
    @Size(max = 10, message = "课程ID不能超过10个字符")
    private String courseId;
    @NotBlank(message = "课程名称不能为空")
    @Size(max = 20, message = "课程名称不能超过20个字符")
    private String courseName;
    @DecimalMin(value = "0.5", message = "学分不能小于0.5")
    @DecimalMax(value = "10.0", message = "学分不能大于10.0")
    private double credit;
    @Size(max=20,message="教师名称不可超过20个字符")
    private String teacher;

    public Course(String courseId, String courseName, double credit, String teacher) {
        this.courseId = courseId;
        this.courseName = courseName;
        this.credit = credit;
        this.teacher = teacher;
    }
    public Course(){
    }
    public String getCourseId() {
        return courseId;
    }
    public void setCourseId(String courseId) {
        this.courseId = courseId;
    }
    public String getCourseName() {
        return courseName;
    }
    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }
    public double getCredit() {
        return credit;
    }
    public void setCredit(double credit) {
        this.credit = credit;
    }
    public String getTeacher() {
        return teacher;
    }
    public void setTeacher(String teacher) {
        this.teacher = teacher;
    }
    @Override
    public String toString() {
        return "Course {course_id=" + courseId + ", course_name=" + courseName + ", credit=" + credit + ", teacher="
                + teacher + "}";
    }
}
