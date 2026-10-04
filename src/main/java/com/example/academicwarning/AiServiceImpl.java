package com.example.academicwarning;
import tools.jackson.databind.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.cache.annotation.Cacheable;

import java.time.Duration;
import java.util.List;
import java.util.Map;
@Service
public class AiServiceImpl implements AiService{
    @Value("${deepseek.api-key}") private String apiKey;
    @Value("${deepseek.api-url}") private String apiUrl;
    @Value("${deepseek.model}")   private String model;
    @Value("${warning.pass-score:60}")  private double passScore;
    @Value("${warning.border-line:10}") private double borderLine;

    @Autowired
    private StudentService studentService;
    private static ClientHttpRequestFactory requestFactory() {
        SimpleClientHttpRequestFactory f = new SimpleClientHttpRequestFactory();
        f.setConnectTimeout(Duration.ofSeconds(5));   // 连 api.deepseek.com 最多等 5 秒
        f.setReadTimeout(Duration.ofSeconds(30));     // 连上后等 AI 出结果最多 30 秒
        return f;
    }
    private final RestClient restClient = RestClient.builder()
            .requestFactory(requestFactory())
            .build();

    @Override
    public String chat(String prompt){
        Map<String,Object> body=Map.of(
                "model",model,
                "messages",List.of(Map.of("role","user","content",prompt))
        );

        JsonNode response;
        try{
            response = restClient.post()
                .uri(apiUrl)
                .header("Authorization", "Bearer " + apiKey)
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .body(JsonNode.class);
        }catch (RestClientException e) {
            throw new AiServiceException("AI 服务调用失败：" + e.getMessage());
        }
        if(response == null){
            throw new AiServiceException("AI 服务返回空响应");
        }
        JsonNode content = response.path("choices").path(0).path("message").path("content");
        if(content.isMissingNode()||content.asText().isBlank()){
            throw new AiServiceException("AI 服务返回空内容");
        }
        return content.asText();
    }
    @Cacheable(value = "aiAnalysis", key = "'all'")
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
    @Cacheable(value = "aiAdvice", key = "#id")
    @Override
    public String adviseStudent(String id){
        Student s=studentService.findById(id);
        StringBuilder sb=new StringBuilder("你是高校教务助手。\n");
        sb.append("学生:").append(s.getId()).append("   ")
                .append((s.getName())).append("   ")
                .append(s.getScore()).append(" 分\n");
        if(s.getScore() < passScore){
            sb.append("该生分数低于及格线 ").append(passScore).append("，已构成学业预警。\n");
        }else if(s.getScore()<passScore+borderLine){
            sb.append("该生分数处于临界区间 ").append(passScore).append("~")
                    .append(passScore+borderLine).append("，有挂科风险。\n");
        }else{
            sb.append("该生成绩正常。\n");
        }
        sb.append("请针对这名学生给出 3 条具体、可执行的学习建议，用中文，回答精简。");
        return chat(sb.toString());
    }
}
