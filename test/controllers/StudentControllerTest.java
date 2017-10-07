package controllers;

import com.google.common.collect.ImmutableMap;
import junit.framework.TestCase;
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
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.*;

public class StudentControllerTest {
    public static Application app;

    @BeforeClass
    public static void startApp() {
        app = Helpers.fakeApplication(Helpers.inMemoryDatabase());
        Helpers.start(app);
    }

    @Test
    public void index() throws Exception {
        // Test GET /student on an empty database.
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/student/list");

        Result result = route(app, request);
        assertTrue(contentAsString(result).contains("No students found"));

        //Student that exists
        Student student = new Student("test");
        student.save();

        //Test that there is a user now
        request = new Http.RequestBuilder()
                .method(GET)
                .uri("/student/list");

        result = route(app, request);
        assertTrue(contentAsString(result).contains("test"));
        //Remove student
        student.delete();
    }

    @Test
    public void create() throws Exception {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/student/new");

        Result result = route(app, request);
        TestCase.assertEquals(OK, result.status());
    }

    @Test
    public void store() throws Exception {
        //Valid post
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .bodyForm(ImmutableMap.of("name","test"))
                .uri("/student");
        request = CSRFTokenHelper.addCSRFToken(request);

        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());

        //Empty post
        Http.RequestBuilder request_empty = Helpers.fakeRequest()
                .method(POST)
                .uri("/student");
        request_empty = CSRFTokenHelper.addCSRFToken(request_empty);

        Result result_empty = route(app, request_empty);
        assertEquals(BAD_REQUEST, result_empty.status());

        //Empty name post
        Http.RequestBuilder request_name_empty = Helpers.fakeRequest()
                .method(POST)
                .bodyForm(ImmutableMap.of("name",""))
                .uri("/student");
        request_name_empty = CSRFTokenHelper.addCSRFToken(request_name_empty);

        Result result_name_empty = route(app, request_name_empty);
        assertEquals(BAD_REQUEST, result_name_empty.status());

        //Name already exists
        Http.RequestBuilder request_duplicate = Helpers.fakeRequest()
                .method(POST)
                .bodyForm(ImmutableMap.of("name","test"))
                .uri("/student");
        request_duplicate = CSRFTokenHelper.addCSRFToken(request_duplicate);

        Result result_duplicate = route(app, request_duplicate);
        assertEquals(BAD_REQUEST, result_duplicate.status());
    }

    @Test
    public void find() throws Exception {
        //Find student that doesn't exists
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/student/find/1");

        Result result = route(app, request);
        TestCase.assertEquals(SEE_OTHER, result.status());

        //Find student that exists
        Student student = new Student("test");
        student.save();
        Http.RequestBuilder request_found = new Http.RequestBuilder()
                .method(GET)
                .uri("/student/find/"+ student.id);

        Result result_found = route(app, request_found);
        TestCase.assertEquals(OK, result_found.status());
        //remove student
        student.delete();
    }

    @Test
    public void search() throws Exception {
        Http.RequestBuilder request = new Http.RequestBuilder()
                .method(GET)
                .uri("/");

        Result result = route(app, request);
        TestCase.assertEquals(OK, result.status());
    }

    @Test
    public void search_post() throws Exception {
        //Student that exists
        Student student = new Student("test");
        student.save();

        //Valid post
        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(POST)
                .bodyForm(ImmutableMap.of("name","test"))
                .uri("/student/search_post");
        request = CSRFTokenHelper.addCSRFToken(request);

        Result result = route(app, request);
        assertEquals(SEE_OTHER, result.status());

        //No user found
        Http.RequestBuilder request_no_user = Helpers.fakeRequest()
                .method(POST)
                .bodyForm(ImmutableMap.of("name","asdf"))
                .uri("/student/search_post");
        request_no_user = CSRFTokenHelper.addCSRFToken(request_no_user);

        Result result_no_user = route(app, request_no_user);
        assertEquals(BAD_REQUEST, result_no_user.status());

        //No post
        Http.RequestBuilder request_no_post = Helpers.fakeRequest()
                .method(POST)
                .uri("/student/search_post");
        request_no_post = CSRFTokenHelper.addCSRFToken(request_no_post);

        Result result_no_post = route(app, request_no_post);
        assertEquals(BAD_REQUEST, result_no_post.status());

        //remove test student
        student.delete();
    }

    @Test
    public void delete() throws Exception {
        //Student to delete
        Student student = new Student("test");
        student.save();

        Http.RequestBuilder request = Helpers.fakeRequest()
                .method(GET)
                .uri("/student/"+ student.id +"/delete");
        request = CSRFTokenHelper.addCSRFToken(request);

        route(app, request);
        Student lookupStudent = Student.find.byId(student.id);
        assertEquals(lookupStudent, null);
    }

    @AfterClass
    public static void stopApp() {
        Helpers.stop(app);
    }
}
