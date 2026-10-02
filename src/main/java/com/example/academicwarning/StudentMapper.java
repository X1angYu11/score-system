package com.example.academicwarning;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * MyBatis 的 Mapper 接口。
 *
 * 和原来的 Dao 有什么区别？
 *   - Dao：接口 + 实现类（StudentDaoImpl 里手写 JdbcTemplate 和 RowMapper）
 *   - Mapper：只写"方法签名 + SQL"，MyBatis 自动生成实现类（不用写 Impl）
 *
 * @Mapper 的作用：告诉 MyBatis 为这个接口生成实现类，并注册成 Spring Bean，
 *                这样 Service 里 @Autowired 就能直接注入使用。
 */
@Mapper
public interface StudentMapper {

    /**
     * 固定条件查询：查成绩在 [minScore, maxScore] 区间内的学生。
     *
     * 语法点：
     *   #{minScore}      —— 等价于 JdbcTemplate 的 ? ：预编译参数，能防 SQL 注入
     *   @Param("名字")    —— 给参数起名，供 #{} 引用（有多个参数时必须写）
     *   BETWEEN a AND b  —— 等价于 score >= a AND score <= b（包头包尾）
     */
    @Select("SELECT id, name, score FROM student WHERE score BETWEEN #{minScore} AND #{maxScore}")
    List<Student> findRange(@Param("minScore") double minScore, @Param("maxScore") double maxScore);

    /**
     * 动态 SQL 查询：三个条件都是可选的，传了哪个条件就拼哪个，没传的不参与。
     * 例：只传 name → WHERE name LIKE '%z%'
     *     只传分数 → WHERE score >= 60 AND score <= 80
     *     都不传   → 没有 WHERE，查全部
     *
     * 语法点：
     *   <script>          在注解里写动态 SQL，必须用这一层包起来
     *   <where>           ① 有条件时自动加 WHERE
     *                     ② 自动去掉第一个多余的 AND
     *                     ③ 所有 <if> 都没命中时，整个 WHERE 不出现
     *   <if test="条件">   条件成立才把里面的 SQL 拼进去（test 里写参数名）
     *   &gt;  &lt;         XML 里不能直接写 > 和 <，必须转义成 &gt; &lt;
     *   Double（包装类）   可选参数不传时是 null；基本类型 double 装不下 null（会报 500）
     */
    @Select("""
        <script>
        SELECT id, name, score FROM student
        <where>
            <if test="name != null and name != ''">
                AND name LIKE CONCAT('%', #{name}, '%')
            </if>
            <if test="minScore != null">
                AND score &gt;= #{minScore}
            </if>
            <if test="maxScore != null">
                AND score &lt;= #{maxScore}
            </if>
        </where>
        ORDER BY id
        </script>
        """)
    List<Student> query(@Param("name") String name,
                        @Param("minScore") Double minScore,
                        @Param("maxScore") Double maxScore);
}
