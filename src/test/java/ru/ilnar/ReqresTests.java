package ru.ilnar;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.*;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

public class ReqresTests {

    public static final String SINGLE_RESOURCE = "/api/unknown";
    public static final String API_USERS = "api/users";
    public static final Integer STATUS_CODE_SUCCESS = 200;
    public static final Integer STATUS_CODE_CREATED = 201;
    public static final Integer STATUS_CODE_UNSUPPORTED_MEDIA_TYPE = 415;

    @BeforeEach
    public void beforeEach(){
        baseURI = "https://reqres.in";
    }

    @Test
    public void getSingleResourceTest() {
        Integer resourceId = 2;
        String resourceName = "fuchsia rose";
        get(SINGLE_RESOURCE + "/" + resourceId.toString())
                .then()
                .log().body()
                .statusCode(STATUS_CODE_SUCCESS)
                .body("data.name", is(resourceName));
    }

    @Test
    public void getUsersListTest(){
        Integer page = 1;
        Integer itemsPerPage = 6;

        given()
        .log().uri()
                .params("page", page.toString())
                .get(API_USERS)
                .then()
                .log().body()
                .statusCode(STATUS_CODE_SUCCESS)
                .body("page", equalTo(page))
                .body("per_page",is(itemsPerPage))
                .body("data.size()", is(itemsPerPage));
    }

    @Test
    public void createUserPostTest() throws JsonProcessingException {
        User user = new User("morpheus", "leader");
        ObjectMapper objectMapper = new ObjectMapper();
        String userJson = objectMapper.writeValueAsString(user);

        given()
                .log().uri()
                .log().body(true)
                .body(userJson)
                .when()
                .post(API_USERS)
                .then()
                .log().body()
                .statusCode(STATUS_CODE_CREATED)
                .body(matchesJsonSchemaInClasspath("schemes/create-user-scheme.json"));
    }

    @Test
    public void createUserPostEmptyBodyTest() {
        given()
                .when()
                .post(API_USERS)
                .then()
                .log().body()
                .statusCode(STATUS_CODE_UNSUPPORTED_MEDIA_TYPE);
    }

    @Test
    public void deleteUserTest(){
        Integer userId = 1;
        given()
                .delete(API_USERS + "/" + userId)
                .then()
                .statusCode(204);
    }
}
