package org.example;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static io.restassured.RestAssured.given;

public class RequestHandler {
    TestEnvironment testEnvironment = new TestEnvironment();

    public String postRequestCreated(JSONObject record, String object) {
        return
                given().
                        contentType(ContentType.JSON).
                        accept(ContentType.JSON).
                        header(Constants.AUTHORIZATION, Constants.BEARER + Constants.ACCESS_TOKEN).
                        header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON).
                        body(record.toString()).
                        when().post(testEnvironment.getEndpoint() + "/services/data/v51.0/sobjects/" + object).
                        then().log().ifValidationFails().statusCode(201).extract().path("id");
    }

    public List<String> postBadRequest(JSONObject record, String object) {
        return
                given().
                        contentType(ContentType.JSON).
                        accept(ContentType.JSON).
                        header(Constants.AUTHORIZATION, Constants.BEARER + Constants.ACCESS_TOKEN).
                        header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON).
                        body(record.toString()).
                        when().post(testEnvironment.getEndpoint() + "/services/data/v51.0/sobjects/" + object).
                        then().log().ifValidationFails().statusCode(400).extract().path("message");
    }

    public ValidatableResponse pathRequest(JSONObject record, String id, String object) {

        return
                given().
                        contentType(ContentType.JSON).
                        accept(ContentType.JSON).
                        header(Constants.AUTHORIZATION, Constants.BEARER + Constants.ACCESS_TOKEN).
                        header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON).
                        body(record.toString()).
                        when().patch(testEnvironment.getEndpoint() + "/services/data/v51.0/sobjects/" + object + "/" + id).
                        then().log().ifValidationFails().statusCode(204);

    }

    public String deleteRecordById(String id, String object) {
        return
                given().
                        contentType(ContentType.JSON).
                        accept(ContentType.JSON).
                        header(Constants.AUTHORIZATION, Constants.BEARER + Constants.ACCESS_TOKEN).
                        header(Constants.CONTENT_TYPE, Constants.APPLICATION_JSON).
                        when().delete(testEnvironment.getEndpoint() + "/services/data/v51.0/sobjects/" + object + "/" + id).
                        then().statusCode(204).toString();
    }

}
