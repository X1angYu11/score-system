package com.example.academicwarning;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;
@RestController
@RequestMapping("/courses")
public class CourseController {
    @Autowired
    private CourseService courseService;
    @GetMapping
    public List<Course> findAll() {
        return courseService.findAll();
    }
    @GetMapping("/{courseId}")
    public Course findById(@PathVariable String courseId) {
        return courseService.findById(courseId);
    }
    @PostMapping()
    public String insert(@Valid @RequestBody Course course) {
        int res= courseService.insert(course);
        return "插入了"+res+"条数据";
    }
    @PutMapping("/{courseId}")
    public String update(@PathVariable String courseId, @Valid @RequestBody Course course) {
        course.setCourseId(courseId);
        int res= courseService.update(course);
        return "更新了"+res+"条数据";
    }
    @DeleteMapping("/{courseId}")
    public String deleteById(@PathVariable String courseId) {
        int res= courseService.deleteById(courseId);
        return "删除了"+res+"条数据";
    }
}
