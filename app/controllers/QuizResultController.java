package controllers;

import forms.QuizForm;
import io.ebean.Ebean;
import io.ebean.SqlRow;
import models.QuizResult;
import models.Student;
import play.data.Form;
import play.data.FormFactory;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import views.html.quiz.create;

import javax.inject.Inject;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This controller contains an action to handle HTTP requests
 *  for Quizzes
 */
public class QuizResultController extends Controller {

    private Form<QuizForm> quizForm;
    @Inject
    public QuizResultController(FormFactory formFactory) {
            this.quizForm = formFactory.form(QuizForm.class);
    }

    public QuizResultController() {

    }

    public Result create() {
        return ok(create.render(quizForm));
    }

    public Result store() {
        Form<QuizForm> quizFormFilled = this.quizForm.bindFromRequest();

        if (quizFormFilled.hasErrors()) {
            return badRequest(create.render(quizFormFilled));
        }

        //Get user input
        String name = quizFormFilled.get().name;
        int correct = Integer.parseInt(quizFormFilled.get().correct);
        int total = Integer.parseInt(quizFormFilled.get().total);

        //Get student
        Student student = Student.find.query().where().eq("name",  name).findUnique();

        //Get last attempt
        QuizResult lastResult = this.get_current_score(student.id);

        //Store new quiz score
        QuizResult newQuiz = new QuizResult();
        newQuiz.student = student;
        newQuiz.correct = correct;
        newQuiz.total = total;
        newQuiz.attempt = (lastResult != null) ? lastResult.attempt + 1 : 1;
        newQuiz.save();

        // Compare users score to last attempt
        double last_score_avg = (lastResult != null) ? (lastResult.correct / (double)lastResult.total) : 0;
        double new_score_avg = (lastResult != null) ? (newQuiz.correct / (double)newQuiz.total) : 0;
        if(lastResult != null){
            if( new_score_avg > last_score_avg ){
                flash("success", "Score is better then before");
            }else if ( new_score_avg < last_score_avg ){
                flash("info", "Score is worse then before");
            }else{
                flash("info", "Score is the same as before");
            }
        }else{
            flash("info", "Score added successfully");
        }
        return redirect(routes.StudentController.find(student.id));
    }

    public Result average(boolean use_last_attempt) {
        // Ebean doesn't have group by ?

        //
        String sql = "SELECT student_id,correct,total FROM quiz_result";
        if(use_last_attempt){
            sql = "SELECT student_id,correct,total, MAX(attempt) FROM quiz_result GROUP BY student_id";
        }
        List<SqlRow> sqlRows;
        sqlRows = Ebean.createSqlQuery(sql)
                .findList();
        int correct = 0;
        int total = 0;

        // Loop though rows and add to vars
        for (SqlRow quiz : sqlRows) {
            correct += quiz.getInteger("correct");
            total += quiz.getInteger("total");
        }

        //Calc the avg
        double avg = 0;
        if(sqlRows.size() > 0){
            avg = (correct / (double) total) * 100;
        }

        //Store findings in map and return as json
        Map<String, Double> scores = new HashMap<>();
        scores.put("correct_sum", (double) correct);
        scores.put("total_sum", (double) total);
        scores.put("average", avg);

        return ok(Json.toJson(scores));
    }

    public QuizResult get_current_score(long studentid) {
        QuizResult quizResult = QuizResult.find.query().where()
                .eq("student_id", studentid).orderBy("attempt desc").setMaxRows(1).findUnique();

        return quizResult;
    }
}
