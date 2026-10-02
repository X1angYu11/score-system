package com.example.academicwarning;
import tools.jackson.databind.JsonNode;  // ⚠️ 见下方说明
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
@Service
public class AiServiceImpl implements AiService{
    @Value("${deepseek.api-key}") private String apiKey;
    @Value("${deepseek.api-url}") private String apiUrl;
    @Value("${deepseek.model}")   private String model;

    @Autowired
    private StudentService studentService;

    private final RestClient restClient=RestClient.create();

    @Override
    public String chat(String prompt){
        Map<String,Object> body=Map.of(
                "model",model,
                "messages",List.of(Map.of("role","user","content",prompt))
        );

        JsonNode response = restClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);
        return response.path("choices").get(0).get("message").path("content").asText();
    }
    @Override
    public String analyzeStudents() {
        List<Student> students = studentService.findAll();
        List<Map<String, Object>> warnings = studentService.warnings();

        StringBuilder sb = new StringBuilder("你是高校教务助手，以下是本班成绩数据：\n");
        for (Student s : students) {
            sb.append(s.getId()).append("  ").append(s.getName())
                    .append("  ").append(s.getScore()).append(" 分\n");
        }
        sb.append("\n需要预警的学生：").append(warnings).append("\n");
        sb.append("请用中文简要分析班级整体成绩情况，并给出 3 条给教师的具体建议。注意一定要精简！");

        return chat(sb.toString());
    }
}
