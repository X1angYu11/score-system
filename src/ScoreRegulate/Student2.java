package ScoreRegulate;

public class Student2 {
    private String id;
    private String name;
    private double score;
    public Student2(String id,String name,double score){
        this.name=name;
        this.id=id;
        this.score=score;
    }
    public void setScore(double score){
        this.score=score;
    }
    public void setName(String name){
        this.name=name;
    }
    public void setId(String id){
        this.id=id;
    }
    public void show(){
        System.out.println("学号：" + id + "，姓名：" + name + "，成绩：" + score);
    }
    public String getId() { return id; }
    public String getName() { return name; }
    public double getScore() { return score; }
}
