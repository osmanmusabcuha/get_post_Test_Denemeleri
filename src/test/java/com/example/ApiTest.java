package com.example;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class ApiTest {
    static String token = null;

    @BeforeAll
    public static void beforeAllOnce(){
        RestAssured.baseURI = "http://localhost:5000";
        token =
                given()
                .contentType(ContentType.JSON)
                .body("{\n" +
                        "  \"email\":\"employee1@example.com\", \"password\":\"test123\"\n" +
                        "}")
                        .log().all()
                .when()
                .post("/api/auth/login")
                .then()
                        .log().all()
                .extract()
                .response()
                .path("token");



    }
    @Test
    public void getAllPhonesWithAuth() {
        given()
                .auth().oauth2(token)

                .accept(ContentType.JSON)
                .log().all()
                .when()
                .get("/api/phones")
                .then()
                .log().all()
                .statusCode(200)
                .body("success", equalTo(true))
                .body("phones[0].brand", equalTo("Apple"))
                .body("phones[1].model", equalTo("Galaxy S21"))
                .time(lessThan(1000L));
    }



    @Test
    public void postPhone_ShouldReturnCreatedStatusAndValidResponse() {
        String uniqueIMEI = "99999999999" + (System.currentTimeMillis() % 10000);
        String jsonBody = "{\n" +
                "  \"brand\": \"Xiaomi\",\n" +
                "  \"model\": \"Redmi Note 12\",\n" +
                "  \"imei\": \"" + uniqueIMEI + "\",\n" +
                "  \"price\": 13500,\n" +
                "  \"warranty\": \"1 yıl\"\n" +
                "}";


        given()
                .auth().oauth2(token)
                .contentType(ContentType.JSON)
                .accept(ContentType.JSON)
                .body(jsonBody)
                .when()
                .post("/api/phones")
                .then()
                .log().all()
                .statusCode(201)
                .body("success", equalTo(true))
                .body("message", containsString("başarıyla"))
                .body("phoneId", notNullValue())
                .time(lessThan(1000L));
    }

}
