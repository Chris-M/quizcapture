package models;

import io.ebean.FetchConfig;
import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import java.util.ArrayList;
import java.util.List;


/**
 * Student entity managed by Ebean
 */
@Entity
public class Student extends Model {
    @Id
    public Long id;

    @Column(unique=true,nullable=false)
    @Constraints.Required
    public String name;

    @OneToMany(mappedBy = "student")
    public List<QuizResult> quizResults= new ArrayList<>();

    /**
     * @param name      The user's email
     */
    public Student(String name){
        this.name = name;
    }

    public static final Finder<Long, Student> find = new Finder<>(Student.class);

    public static List<Student> getAllstudents(){
        List<Student> students =  new ArrayList<Student>();
        students = Student.find.query()
                .fetch("quizResults", new FetchConfig().query())
                .findList();

        return students;
    }

}

