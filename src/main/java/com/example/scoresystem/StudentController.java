package com.example.scoresystem;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.bind.annotation.PutMapping;//改
import  org.springframework.web.bind.annotation.DeleteMapping;//删

import java.util.List;
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
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"学生不存在");
        }
        return stu;
    }
    @GetMapping("/students/search")
    public Student studentfindbyname(@RequestParam String name){
        Student stu= studentService.findByName(name);
        if(stu==null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"没找到该姓名的学生");
        }
        return stu;
    }
    @PostMapping("/students")
    public String insert(@RequestBody Student stu){
        studentService.insert(stu);
        return "添加成功"+stu.getName();
    }
    @PutMapping("/students/{id}/score")
    public String update(@PathVariable String id,@RequestParam double score){
        studentService.updateScore(id,score);
        return "修改成功"+id;
    }
    @GetMapping("/students/sort")
    public List<Student> sort(){
        return studentService.findAllByScoreDesc();
    }
    @DeleteMapping("/students/{id}")
    public String delete(@PathVariable String id){
        studentService.deleteById(id);
        return "删除成功"+id;
    }
}
