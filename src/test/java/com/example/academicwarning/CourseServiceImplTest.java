package com.example.academicwarning;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CourseServiceImplTest {
    @Mock
    private CourseMapper courseMapper;
    @InjectMocks
    private CourseServiceImpl courseService;
    @Test
    void findById_课程存在_应返回课程(){
        Course C=new Course("C001","高等数学",4.0,"张老师");
        when(courseMapper.findById("C001")).thenReturn(C);

        Course result=courseService.findById("C001");

        assertEquals("高等数学",result.getCourseName());
    }
    @Test
    void findById_课程不存在_应抛CourseNotFoundException(){
        assertThrows(CourseNotFoundException.class,()->courseService.findById("C099"));
    }
    @Test
    void update_影响0行_应抛CourseNotFoundException(){
        Course C=new Course("C999","不存在的课",3.0,"T");
        when(courseMapper.update(C)).thenReturn(0);
        assertThrows(CourseNotFoundException.class,()->courseService.update(C));
    }
    @Test
    void deleteById_影响0行_应抛CourseNotFoundException(){
        Course C=new Course("C999","不存在的课",3.0,"T");
        when(courseMapper.deleteById("C999")).thenReturn(0);
        assertThrows(CourseNotFoundException.class,()->courseService.deleteById("C999"));
    }
    @Test
    void findAll_应把结果原样返回(){
        List<Course> courses=List.of(new Course("C001","高等数学",4.0,"张老师")
                ,new Course("C002","线性代数",3.0,"李老师"));
        when(courseMapper.findAll()).thenReturn(courses);
        assertEquals(courses,courseService.findAll());

    }
}
