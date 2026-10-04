package com.example.academicwarning;
import java.util.List;
public interface AiService {
    String chat(String prompt);

    String analyzeStudents();

    String adviseStudent(String id);
}
