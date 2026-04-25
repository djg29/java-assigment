package com.fulfilment.application.monolith.fulfillment.controller;

import com.fulfilment.application.monolith.fulfillment.FullfilmentFailureException;
import com.fulfilment.application.monolith.fulfillment.model.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.service.FulFillmentService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;
import static io.restassured.RestAssured.given;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.hamcrest.CoreMatchers.is;

@QuarkusTest
class FulfillmentControllerTest {

    @Inject
    FulfillmentController controller;               // real controller, injected by Quarkus

    @InjectMock
    FulFillmentService service;                     // mocked service, overrides the real bean

    @Test
    void getAllfulfillments_shouldReturnListFromService() {
        // Given
        List<Fulfillment> expectedList = List.of(new Fulfillment("code", "product", "store"), new Fulfillment("code1", "product1", "store1"));
        when(service.listAllFulfillments()).thenReturn(expectedList);

        given()
                .when().get("/fulfillment")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void create_withValidFulfillment_shouldReturn201() {
        Fulfillment validFulfillment = new Fulfillment("code", "prodcut", "sto");
        when(service.createFulfillment(validFulfillment)).thenReturn(validFulfillment);

        given()
                .contentType(ContentType.JSON)
                .body(validFulfillment)
                .when().post("/fulfillment")
                .then()
                .statusCode(201)
                .body("businessUnitCode", is("code"))
//                .body("productName", is("product"))
                .body("storeName", is("sto"));

        verify(service, times(1)).createFulfillment(any(Fulfillment.class));
    }

    @Test
    void create_withMissingBusinessUnitCode_shouldReturn400() {
        Fulfillment invalid = new Fulfillment(null, "prod", "store");

        given()
                .contentType(ContentType.JSON)
                .body(invalid)
                .when().post("/fulfillment")
                .then()
                .statusCode(400);  // Bean Validation failure -> ConstraintViolationException -> mapped to 400 by Quarkus default
    }

    @Test
    void create_withEmptyBusinessUnitCode_shouldReturn400() {
        Fulfillment invalid = new Fulfillment("", "prod", "store");

        given()
                .contentType(ContentType.JSON)
                .body(invalid)
                .when().post("/fulfillment")
                .then()
                .statusCode(400);  // Bean Validation failure -> ConstraintViolationException -> mapped to 400 by Quarkus default
    }

    @Test
    void create_whenServiceThrowsFullfilmentFailureException_shouldReturn400WithMessage() {
        Fulfillment fulfillment = new Fulfillment("bus", "prod", "store");
        doThrow(new FullfilmentFailureException("Assignment already exists"))
                .when(service).createFulfillment(any(Fulfillment.class));

        given()
                .contentType(ContentType.JSON)
                .body(fulfillment)
                .when().post("/fulfillment")
                .then()
                .statusCode(400);
    }

    @Test
    void create_whenServiceThrowsException_shouldPropagateException() {
        // Given
        Fulfillment fulfillment = new Fulfillment("code", "product", "store");
        doThrow(new WebApplicationException("DB error")).when(service).createFulfillment(any(Fulfillment.class));

        given()
                .contentType(ContentType.JSON)
                .body(fulfillment)
                .when().post("/fulfillment")
                .then()
                .statusCode(500);
    }
}