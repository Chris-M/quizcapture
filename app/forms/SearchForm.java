package forms;

import models.Student;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import java.util.ArrayList;
import java.util.List;

public class SearchForm {

    @Constraints.Required(message="validation.required")
    @Constraints.MaxLength(value=100,message="validation.maxLength")
    public String name;

    /**
     * This method validates a form to register a student
     * @return A list of errors by field
     */
    public List<ValidationError> validate(){
        List<ValidationError> errors = new ArrayList<ValidationError>();
        Student student = Student.find.query().where().eq("name",  name).findUnique();
        // Student not found created
        if (student == null)
            errors.add(new ValidationError("name", "validation.name.notFound"));

        return errors.isEmpty() ? null : errors;
    }
}