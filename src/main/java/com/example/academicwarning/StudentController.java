package com.example.academicwarning;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PutMapping;//改
import  org.springframework.web.bind.annotation.DeleteMapping;//删
import jakarta.validation.Valid;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
public class StudentController {
    @Autowired
    private StudentService studentService;
    @GetMapping("/students")
    public List<Student> studentfindall(){
        return studentService.findAll();
    }
    @GetMapping("/students/{id}")
    public Student studentfindbyid(@PathVariable String id){
        Student stu= studentService.findById(id);
        if(stu==null){
            throw new StudentNotFoundException("学生"+id+"不存在");
        }
        return stu;
    }
    @GetMapping("/students/search")
    public Student studentfindbyname(@RequestParam String name){
        Student stu= studentService.findByName(name);
        if(stu==null){
            throw new StudentNotFoundException("没找到姓名为"+name+"的学生");
        }
        return stu;
    }
    @PostMapping("/students")
    public String insert(@Valid @RequestBody Student stu){
        studentService.insert(stu);
        return "添加成功"+stu.getName();
    }
    @PutMapping("/students/{id}/score")
    public String update(@PathVariable String id,@RequestParam double score){
        int rows=studentService.updateScore(id,score);
        if(rows==0){
            throw new StudentNotFoundException("学生"+id+"不存在，修改失败");
        }
        return "修改成功：" + id;
    }
    @GetMapping("/students/sort")
    public List<Student> sort(){
        return studentService.findAllByScoreDesc();
    }
    @DeleteMapping("/students/{id}")
    public String delete(@PathVariable String id){
        int rows= studentService.deleteById(id);
        if(rows==0){
            throw new StudentNotFoundException("学生"+id+"不存在，无需删除");
        }
        return "删除成功：" + id;
    }
    @PostMapping("/students/batch")
    public String insertBatch(@RequestBody List<Student> students){
        int count=studentService.insertBatch(students);
        return "批量导入成功，共 " + count + " 条";
    }
    @GetMapping("/students/page")
    public Map<String, Object> findPage(@RequestParam(defaultValue = "1") int page,
                                        @RequestParam(defaultValue = "3") int size){
        Map<String,Object> result=new LinkedHashMap<>();
        result.put("total",studentService.count());
        result.put("page",page);
        result.put("size",size);
        result.put("data",studentService.findPage(page,size));
        return result;
    }
    @GetMapping("/students/stats")
    public Map<String, Object> stats(){
        Map<String, Object> result=studentService.stats();
        return result;
    }
    @GetMapping("/students/top")
    public List<Map<String, Object>> findTop(@RequestParam int n){
        return studentService.findTop(n);
    }
    @GetMapping("/students/warnings")
    public List<Map<String,Object>> warnings(){
        return studentService.warnings();
    }

}
