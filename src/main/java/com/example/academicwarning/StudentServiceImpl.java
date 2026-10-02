package com.example.academicwarning;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;


@Service
public class StudentServiceImpl implements StudentService{
    @Autowired
    private StudentDao studentDao;
    @Autowired
    private StudentMapper studentMapper;
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
    @CacheEvict(value = "stats", key = "'all'")
    public int insert(Student stu){
        return studentDao.insert(stu);
    }
    @Override
    @CacheEvict(value = "stats", key = "'all'")
    public int updateScore(String id,double score){
        return studentDao.updateScore(id,score);
    }
    @Override
    @CacheEvict(value = "stats", key = "'all'")
    public int deleteById(String id){
        return studentDao.deleteById(id);
    }
    @Override
    public List<Student> findAllByScoreDesc(){
        return studentDao.findAllByScoreDesc();
    }
    @Override
    @Transactional
    @CacheEvict(value = "stats", key = "'all'")
    public int insertBatch(List<Student> students){
        int count=0;
        for(Student stu:students){
            count+=studentDao.insert(stu);
        }
        return count;
    }
    @Override
    public List<Student> findPage(int page,int size){
        if(page<1){
            page=1;
        }
        if(size<1){
            size=10;
        }
        if(size>100){
            size=100;
        }
        int offset=(page-1)*size;
        return studentDao.findPage(offset,size);
    }
    @Override
    public int count(){
        return studentDao.count();
    }
    @Cacheable(value = "stats", key = "'all'")
    @Override
    public Map<String,Object> stats(){
        log.info("【缓存未命中】正在查数据库计算统计");
        Map<String,Object> raw=studentDao.stats();
        long total=toLong(raw.get("total"));
        long passCount=toLong(raw.get("passCount"));
        double avg= toDouble(raw.get("avgScore"));
        double max=toDouble(raw.get("maxScore"));
        double min=toDouble(raw.get("minScore"));

        Map<String,Object> result=new LinkedHashMap<>();
        result.put("total",total);
        result.put("avgScore",round2(avg));
        result.put("maxScore",max);
        result.put("minScore",min);
        result.put("passCount",passCount);
        result.put("passRate",total==0?0.0:round1(passCount*100.0/total));
        return result;
    }
    private long toLong(Object value){
        return value==null?0L:((Number)value).longValue();
    }
    private double toDouble(Object value){
        return value==null?0.0:((Number)value).doubleValue();
    }
    private double round1(double value){
        return Math.round(value*10)/10.0;
    }
    private double round2(double value){
        return Math.round(value*100)/100.0;
    }
    @Override
    public List<Map<String,Object>> findTop(int n){
        if(n<1){
            n=3;
        }else if(n>100){
            n=100;
        }
        List<Student> list=studentDao.findTop(n);
        List<Map<String,Object>> result=new ArrayList<>();
        int rank=1;
        for(int i=0;i<list.size();i++){
            Student stu=list.get(i);
            if(i>0&&stu.getScore()<list.get(i-1).getScore()){
                rank=i+1;
            }
            Map<String,Object> row=new LinkedHashMap<>();
            row.put("rank",rank);
            row.put("id",stu.getId());
            row.put("name",stu.getName());
            row.put("score",stu.getScore());
            result.add(row);
        }
        return result;
    }
    @Value("${warning.pass-score:60}")
    private double passScore;

    @Value("${warning.border-line:10}")
    private double borderLine;

    @Override
    public List<Map<String,Object>> warnings(){
        List<Student> all=studentDao.findAll();
        List<Map<String,Object>> result=new ArrayList<>();
        for(Student s:all){
            String level=null;
            if(s.getScore()<passScore){
                level="不及格";
            }else if(s.getScore()<passScore+borderLine){
                level="临界";
            }
            if(level==null){
                continue;
            }
            Map<String,Object> row=new LinkedHashMap<>();
            row.put("id",s.getId());
            row.put("name",s.getName());
            row.put("score",s.getScore());
            row.put("level",level);
            result.add(row);
        }
        return result;
    }
    @Override
    public List<Student> findRange(double minScore, double maxScore){
        if(minScore<=maxScore){
            return studentMapper.findRange(minScore, maxScore);
        }else{
            throw new BadRequestException("Invalid score range");
        }
    }
    @Override
    public List<Student> query(String name, Double minScore, Double maxScore){
        return studentMapper.query(name, minScore, maxScore);
    }
    private static final Logger log = LoggerFactory.getLogger(StudentServiceImpl.class);


}
