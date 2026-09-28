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
class StudentServiceImplTest {
    @Mock
    private StudentDao studentDao;

    @InjectMocks StudentServiceImpl studentService;

    @Test
    void findRange_最小值大于最大值_应抛异常(){
        assertThrows(BadRequestException.class,()->studentService.findRange(90,60));
    }

    @Test
    void findPage_页码小于1_应修正为第1页(){
        when(studentDao.findPage(0,5)).thenReturn(List.of());

        studentService.findPage(0,5);

        verify(studentDao).findPage(0,5);
    }
    @Test
    void findPage_每页条数超过上限_应限制为100(){
        when(studentDao.findPage(0, 100)).thenReturn(List.of());   // ①
        studentService.findPage(1, 500);                           // ②
        verify(studentDao).findPage(0, 100);
    }
}
