package forms;

import models.Student;
import play.data.validation.Constraints;
import play.data.validation.ValidationError;

import play.data.validation.Constraints.Validate;
import play.data.validation.Constraints.Validatable;

import java.util.ArrayList;
import java.util.List;

@Validate
public class StudentForm implements Validatable<List<ValidationError>> {

    @Constraints.Required(message="validation.required")
    @Constraints.MaxLength(value=100,message="validation.maxLength")
    public String name;

    /**
     * This method validates a form to register a student
     * @return A list of errors by field
     */
    @Override
    public List<ValidationError> validate(){
        List<ValidationError> errors = new ArrayList<ValidationError>();
        Student student = Student.find.query().where().eq("name",  name).findUnique();
        // Already created
        if (student != null)
            errors.add(new ValidationError("name", "validation.name.alreadyExist"));

        return errors.isEmpty() ? null : errors;
    }
}