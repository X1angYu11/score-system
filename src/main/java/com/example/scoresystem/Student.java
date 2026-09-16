package com.example.scoresystem;

/**
 * 学生实体，对应数据库 score_db 里的 student 表 (id, name, score)。
 *
 * 为什么必须同时有 getter 和 setter：
 *   - getter  → Jackson 把对象转成 JSON 时用（接口返回数据给前端）
 *   - setter  → Jackson 把 JSON 转成对象时用（前端提交数据给后端）
 *
 * Controller 里的 @RequestBody Student 依赖 setter 才能把请求体写进对象。
 * 只有 getter 的话，Spring Boot 默认关闭了 FAIL_ON_UNKNOWN_PROPERTIES，
 * 不会报错，而是静默构造出一个 id=null / name=null / score=0.0 的空对象。
 */
public class Student {
    private String id;
    private String name;
    private double score;

    /** 无参构造器：Jackson 反序列化时需要一个不传参数的构造方式 */
    public Student() {
    }

    /** 全参构造器：自己 new 对象、以及 JdbcTemplate 的行映射 (rs, rowNum) -> new Student(...) 时用 */
    public Student(String id, String name, double score) {
        this.id = id;
        this.name = name;
        this.score = score;
    }

    // ===== getter：序列化（对象 → JSON）时用 =====
    public String getId() { return id; }
    public String getName() { return name; }
    public double getScore() { return score; }

    // ===== setter：反序列化（JSON → 对象）时用，@RequestBody 靠的就是它们 =====
    public void setId(String id) { this.id = id; }
    public void setName(String name) { this.name = name; }
    public void setScore(double score) { this.score = score; }

    /** 调试用：在日志或控制台打印对象时能看到实际内容，而不是 Student@1a2b3c */
    @Override
    public String toString() {
        return "Student{id='" + id + "', name='" + name + "', score=" + score + "}";
    }
}
