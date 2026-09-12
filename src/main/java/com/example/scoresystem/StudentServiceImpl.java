package com.example.scoresystem;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
@Service
public class StudentServiceImpl implements StudentService{
    @Autowired
    private StudentDao studentDao;
    @Override
    public List<Student> findAll(){
        return studentDao.findAll();
    }
    @Override
    public Student findById(String id){
        return studentDao.findById(id);
    }
    @Override
    public Student findByName(String name){
        return studentDao.findByName(name);
    }
    @Override
    public int insert(Student stu){
        return studentDao.insert(stu);
    }
    @Override
    public int updateScore(String id,double score){
        return studentDao.updateScore(id,score);
    }
    @Override
    public int deleteById(String id){
        return studentDao.deleteById(id);
    }
    @Override
    public List<Student> findAllByScoreDesc(){
        return studentDao.findAllByScoreDesc();
    }
    @Override
    @Transactional
    public int insertBatch(List<Student> students){
        int count=0;
        for(Student stu:students){
            count+=studentDao.insert(stu);
        }
        return count;
    }
}
