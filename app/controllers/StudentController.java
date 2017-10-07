package controllers;

import forms.SearchForm;
import forms.StudentForm;
import models.QuizResult;
import models.Student;
import play.data.Form;
import play.data.FormFactory;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.student.create;
import views.html.student.index;
import views.html.student.search;
import views.html.student.show;

import javax.inject.Inject;
import java.util.List;

/**
 * This controller contains an action to handle HTTP requests
 *  for students
 */
public class StudentController extends Controller {

    private final Form<StudentForm> studentForm;
    private final Form<SearchForm> searchForm;

    @Inject
    public StudentController(FormFactory formFactory) {
        this.studentForm = formFactory.form(StudentForm.class);
        this.searchForm = formFactory.form(SearchForm.class);
    }

    public Result index() {
        // Find all Students
        List<Student> students = Student.getAllstudents();
        return ok(index.render(students));
    }

    public Result create() {
        return ok(create.render(studentForm));
    }

    public Result store() {
        Form<StudentForm> studentFormFilled = this.studentForm.bindFromRequest();

        if (studentFormFilled.hasErrors()) {
            return badRequest(create.render(studentFormFilled));
        }

        String name = (studentFormFilled.get().name);
        Student student = new Student(name);
        student.save();

        flash("success", "Student added!");
        return redirect(routes.StudentController.index());
    }

    public Result find(long id) {
        // Find a student by ID
        Student student = Student.find.byId(id);

        if(student == null){
            flash("error", "Student not found!");
            return redirect(routes.StudentController.index());
        }

        return ok(show.render(student));
    }

    public Result search(){
        // Search for user by name
        return ok(search.render(searchForm));
    }

    public Result search_post(){
        Form<SearchForm> searchFormFilled = this.searchForm.bindFromRequest();

        if (searchFormFilled.hasErrors()) {
            return badRequest(search.render(searchFormFilled));
        }

        String name = (searchFormFilled.get().name);
        Student student = Student.find.query().where().eq("name", name).findOne();

        return redirect(routes.StudentController.find(student.id));
    }

    public Result delete(long id) {
        // Delete a student by ID
        QuizResult.find.query().where().eq("student_id", id).delete();
        Student.find.ref(id).delete();

        flash("error", "Student removed!");
        return redirect(routes.StudentController.index());
    }
}
