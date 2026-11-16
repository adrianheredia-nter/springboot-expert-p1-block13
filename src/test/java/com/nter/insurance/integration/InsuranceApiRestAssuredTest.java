package com.nter.insurance.integration;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.annotation.DirtiesContext;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.notNullValue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class InsuranceApiRestAssuredTest {

    @LocalServerPort
    private int port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        RestAssured.basePath = "/api";
    }

    @Test
    void listsSeededClients() {
        given()
                .when().get("/clients")
                .then()
                .statusCode(200)
                .body("$", hasSize(2))
                .body("dni", hasItems("12345678Z", "87654321X"));
    }

    @Test
    void createsClientAndPolicyThroughTheApi() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"idClient": 20, "dni": "22222222J", "name": "Marta", "surname": "Vidal", "birthdate": "1995-07-30"}
                        """)
                .when().post("/clients")
                .then()
                .statusCode(201)
                .body("idClient", equalTo(20))
                .body("birthdate", equalTo("1995-07-30"));

        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"numPolicy": "POL-2000", "branch": "home", "subBranch": "building", "idClient": 20}
                        """)
                .when().post("/policies")
                .then()
                .statusCode(201)
                .body("idPolicy", notNullValue())
                .body("idClient", equalTo(20));

        given()
                .when().get("/clients/20/policies")
                .then()
                .statusCode(200)
                .body("$", hasSize(1))
                .body("[0].numPolicy", equalTo("POL-2000"));
    }

    @Test
    void returnsNotFoundForUnknownClient() {
        given()
                .when().get("/clients/999")
                .then()
                .statusCode(404)
                .body("message", equalTo("Client 999 not found"));
    }

    @Test
    void rejectsDuplicatedPolicyNumber() {
        given()
                .contentType(ContentType.JSON)
                .body("""
                        {"numPolicy": "POL-0001", "branch": "health", "idClient": 2}
                        """)
                .when().post("/policies")
                .then()
                .statusCode(409);
    }
}
