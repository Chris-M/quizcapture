package models;

import io.ebean.Finder;
import io.ebean.Model;
import play.data.validation.Constraints;

import javax.persistence.*;


/**
 * Company entity managed by Ebean
 */
@Table(
        uniqueConstraints=
        @UniqueConstraint(columnNames={"student_id", "attempt"})
)
@Entity
public class QuizResult extends Model {
    @Id
    public Long id;

    @ManyToOne
    public Student student;

    @Constraints.Required
    public int attempt;

    @Constraints.Required
    public int correct;

    @Constraints.Required
    public int total;

    public static final Finder<Long, QuizResult> find = new Finder<>(QuizResult.class);

    @Column(name = "incorrect")
    public Integer getIncorrect() {
        return this.total - this.correct;
    }

}

