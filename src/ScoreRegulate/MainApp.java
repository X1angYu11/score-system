package ScoreRegulate;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Scanner;
import java.sql.*;


public class MainApp {
    public static void main(String[] args) {
        ArrayList<Student2> student2s = new ArrayList<>();
        String url="jdbc:mysql://localhost:3306/score_db?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8";
        try(Connection conn=DriverManager.getConnection(url,"root","123456")){
            Statement st=conn.createStatement();
            ResultSet rs=st.executeQuery("select*from student");
            while(rs.next()){
                student2s.add(new Student2(rs.getString("id"),rs.getString("name"),rs.getDouble("score")));

            }
            System.out.println("📥 已从数据库加载 " + student2s.size() + " 个学生");
        }catch (Exception e){
            System.out.println("⚠️ 数据库加载失败：" + e.getMessage());
        }
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n===== 学生成绩管理系统 =====");
            System.out.println("1. 添加学生");
            System.out.println("2. 显示所有学生");
            System.out.println("3. 按学号删除");
            System.out.println("4. 按姓名查找");
            System.out.println("5. 修改成绩");
            System.out.println("6. 按成绩排序");
            System.out.println("7. 退出");

            try {

                int choice = scanner.nextInt();

                if (choice == 1) {
                    System.out.println("请输入学号");
                    String id = scanner.next();
                    System.out.println("请输入姓名");
                    String name = scanner.next();
                    System.out.println("请输入成绩");
                    double score = scanner.nextDouble();
                    Student2 newS=new Student2(id,name,score);
                    student2s.add(newS);
                    dbInsert(newS);
                    System.out.println("✅ 添加成功！");
                } else if (choice == 2) {
                    if (student2s.isEmpty()) {
                        System.out.println("还没有学生哦");
                    } else {
                        for (int i = 0; i < student2s.size(); i++) {
                            student2s.get(i).show();
                        }
                    }
                } else if (choice == 3) {
                    System.out.println("请输入要删除的学号:");
                    String id = scanner.next();
                    boolean found = false;
                    for (int i = 0; i < student2s.size(); i++) {
                        if (student2s.get(i).getId().equals(id)) {
                            student2s.remove(i);
                            dbDelete(id);
                            found = true;
                            System.out.println("✅ 已删除学号 " + id + " 的学生！");
                            break;
                        }
                    }
                    if (!found) {
                        System.out.println("没找到学号" + id);
                    }
                } else if (choice == 4) {
                    System.out.println("请输入要查找的姓名:");
                    String name = scanner.next();
                    boolean found = false;
                    for (int i = 0; i < student2s.size(); i++) {
                        if (student2s.get(i).getName().equals(name)) {
                            student2s.get(i).show();
                            found = true;

                        }
                    }
                    if (!found) {
                        System.out.println("没找到姓名" + name);
                    }
                } else if (choice==5) {
                    System.out.println("请输入要修改成绩的学号：");
                    String id= scanner.next();
                    boolean found=false;
                    for(int i=0;i<student2s.size();i++){
                        if(student2s.get(i).getId().equals(id)){
                            System.out.println("输入要修改的成绩：");
                            double ns=scanner.nextDouble();
                            student2s.get(i).setScore(ns);
                            dbUpdate(id,ns);
                            found=true;
                            System.out.println("✅ 已修改学号 " + id + " 的成绩！");
                            break;
                        }
                    }
                    if(!found){
                        System.out.println("❌ 没找到学号 " + id);
                    }
                } else if (choice==6) {
                    Collections.sort(student2s,(s1,s2)->Double.compare(s2.getScore(), s1.getScore()));
                    System.out.println("已按照成绩高低排列");
                    for(int i=0;i<student2s.size();i++){
                        student2s.get(i).show();
                    }
                } else if (choice == 7) {
                    System.out.println("👋 再见！");
                    break;
                } else {
                    System.out.println("❌ 输入无效，请输入1-7");
                }
            } catch (Exception e) {
                System.out.println("❌ 请输入有效数字！");
                scanner.nextLine();
            }

        }
    }
    //改动2
    public static void dbInsert(Student2 s){
        String url="jdbc:mysql://localhost:3306/score_db?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8";
        try(Connection conn=DriverManager.getConnection(url,"root","123456")){
            Statement st= conn.createStatement();
            String sql="insert into student (id,name,score) values ('"+s.getId()+"','"+s.getName()+"',"+s.getScore()+")";
            st.executeUpdate(sql);
            System.out.println("💾 已同步到数据库");
        }catch (Exception e){
            System.out.println("⚠️ 数据库同步失败：" + e.getMessage());
        }
    }
    public static void dbDelete(String id){
        String url="jdbc:mysql://localhost:3306/score_db?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8";
        try(Connection conn=DriverManager.getConnection(url,"root","123456")){
            Statement st= conn.createStatement();
            st.executeUpdate("delete from student where id='"+id+"'");
            System.out.println("💾 已从数据库删除");
        }catch (Exception e){
            System.out.println("⚠️ 数据库同步失败：" + e.getMessage());
        }
    }
    public static void dbUpdate(String id,Double newScore){
        String url="jdbc:mysql://localhost:3306/score_db?useSSL=false&allowPublicKeyRetrieval=true&characterEncoding=utf8";
        try(Connection conn=DriverManager.getConnection(url,"root","123456")){
            Statement st= conn.createStatement();
            st.executeUpdate("update student set score=" + newScore + " where id='" + id + "'");
            System.out.println("💾 成绩已同步到数据库");
        }catch (Exception e){
            System.out.println("⚠️ 数据库同步失败：" + e.getMessage());
        }
    }
}
