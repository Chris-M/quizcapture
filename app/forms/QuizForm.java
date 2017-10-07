package forms;

import models.Student;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class QuizForm {

    @Constraints.Required(message="validation.required")
    @Constraints.MaxLength(value=100,message="validation.maxLength")
    public String name;

    @Constraints.Required(message="validation.required")
    @Constraints.Pattern(value="[0-9]+",message="validation.notInt")
    @Constraints.MaxLength(value=100)
    public String correct;

    @Constraints.Required(message="validation.required")
    @Constraints.Pattern(value="[0-9]+",message="validation.notInt")
    @Constraints.MaxLength(value=100)
    public String total;

    /**
     * This method validates a form to register a student
     * @return A list of errors by field
     */
    public List<ValidationError> validate(){
        List<ValidationError> errors = new ArrayList<ValidationError>();
        Student student = Student.find.query().where().eq("name",  name).findUnique();
        // Student not found
        if (student == null)
            errors.add(new ValidationError("name", "validation.name.notFound"));

        int input_correct = Integer.parseInt(correct);
        int input_total = Integer.parseInt(total);
        if(input_total < input_correct)
            errors.add(new ValidationError("total", "validation.total.lessThanCorrect"));

        return errors.isEmpty() ? null : errors;
    }
}