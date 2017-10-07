package controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import junit.framework.TestCase;
import models.QuizResult;
import models.Student;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import play.Application;
import play.api.test.CSRFTokenHelper;
import play.mvc.Http;
import play.mvc.Result;
import play.test.Helpers;

import static org.junit.Assert.assertEquals;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;
import static play.mvc.Http.Status.SEE_OTHER;
import static play.test.Helpers.*;

public class QuizResultControllerTest {
    public static Application app;
    public static Student testStudent;
    private static final double DELTA = 1e-15;

    @BeforeClass
    public static void startApp() {
        app = Helpers.fakeApplication(Helpers.inMemoryDatabase("default", ImmutableMap.of("MODE", "MySQL")));
        Helpers.start(app);
        //Add user for testing
        testStudent = new Student("test");
        testStudent.save();
    }

    @AfterClass
    public static void stopApp() {
        Helpers.stop(app);
    }

    @Test
    public void create() throws Exception {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/score/new");

        Result result = route(app, request);
        TestCase.assertEquals(OK, result.status());
    }

    @Test
    public void store() throws Exception {
        //Valid post
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .bodyForm(ImmutableMap.of("name","test","correct","1","total","2"))
                .uri("/score");
        request = CSRFTokenHelper.addCSRFToken(request);

        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());

        //Empty post
        request = Helpers.fakeRequest()
                .method(POST)
                .uri("/score");
        request = CSRFTokenHelper.addCSRFToken(request);

        result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());

        //Not valid user
        request = Helpers.fakeRequest()
                .method(POST)
                .bodyForm(ImmutableMap.of("name","no real","correct","1","total","2"))
                .uri("/score");
        request = CSRFTokenHelper.addCSRFToken(request);

        result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());

        //Total less then correct
        request = Helpers.fakeRequest()
                .method(POST)
                .bodyForm(ImmutableMap.of("name","test","correct","2","total","1"))
                .uri("/score");
        request = CSRFTokenHelper.addCSRFToken(request);

        result = route(app, request);
        assertEquals(BAD_REQUEST, result.status());
    }

    @Test
    public void average() throws Exception {
        // Get average with no users
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/score/average/false");

        Result result = route(app, request);
        TestCase.assertEquals(OK, result.status());

        String jsonString = contentAsString(result);

        ObjectMapper mapper = new ObjectMapper();
        JsonNode actualObj = mapper.readTree(jsonString);

        System.out.println(actualObj);
        assertEquals(0.0,Double.parseDouble(String.valueOf(actualObj.get("average"))),DELTA );

        //Extra users for testing avg
        Student user1 = new Student("user1");
        user1.save();
        Student user2 = new Student("user2");
        user2.save();
        //Set scores for users
        QuizResult quiz1 = new QuizResult();
        quiz1.student = user1;
        quiz1.correct = 1;
        quiz1.total = 2;
        quiz1.attempt = 1;
        quiz1.save();

        QuizResult quizUser2 = new QuizResult();
        quizUser2.student = user2;
        quizUser2.correct = 1;
        quizUser2.total = 2;
        quizUser2.attempt = 1;
        quizUser2.save();

        QuizResult quiz2 = new QuizResult();
        quiz2.student = user1;
        quiz2.correct = 2;
        quiz2.total = 2;
        quiz2.attempt = 2;
        quiz2.save();

        //Seems to be a bug or issue with using the inMemoryDatabase and Group By
        // TODO: lastAttemptAvg find work around
        double lastAttemptAvg = (quiz1.correct+quiz2.correct) / (double)(quiz1.total+quiz2.total) * 100;
        double allAttemptAvg = ((quiz1.correct+ quizUser2.correct +quiz2.correct) /
                                (double)((quiz1.total+quizUser2.total+quiz2.total)) * 100);

        //All attempt avg test
        request = new Http.RequestBuilder()
                .method(GET)
                .uri("/score/average/false");

        result = route(app, request);
        TestCase.assertEquals(OK, result.status());

        jsonString = contentAsString(result);

        mapper = new ObjectMapper();
        actualObj = mapper.readTree(jsonString);
        assertEquals(allAttemptAvg,Double.parseDouble(String.valueOf(actualObj.get("average"))),DELTA );

        //Remove test quiz
        quiz1.delete();
        quiz2.delete();
        quizUser2.delete();
    }

    @Test
    public void get_current_score() throws Exception {
        QuizResultController quizResultController = new QuizResultController();
        QuizResult current_score = quizResultController.get_current_score(testStudent.id);

        assertEquals(current_score, null);

        //Set scores for users
        QuizResult quiz1 = new QuizResult();
        quiz1.student = testStudent;
        quiz1.correct = 1;
        quiz1.total = 2;
        quiz1.attempt = 1;
        quiz1.save();

        current_score = quizResultController.get_current_score(testStudent.id);
        assertEquals(1, current_score.attempt);

        //remove test quiz
        quiz1.delete();
    }
}