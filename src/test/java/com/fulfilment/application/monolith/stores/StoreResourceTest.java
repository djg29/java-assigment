package com.fulfilment.application.monolith.stores;

import io.quarkus.panache.mock.PanacheMock;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import io.restassured.http.ContentType;
import jakarta.inject.Inject;
import jakarta.transaction.TransactionSynchronizationRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;
import static org.mockito.Mockito.*;
import static io.restassured.RestAssured.given;
import static org.mockito.Mockito.when;

@QuarkusTest
public class StoreResourceTest {

    @Inject
    StoreResource resource;

    @InjectMock
    LegacyStoreManagerGateway legacy;

    @InjectMock
    TransactionSynchronizationRegistry reg;

    @BeforeEach
    void setupMocks() {
        PanacheMock.mock(Store.class);
    }

    @Test
    void getAllStores_shouldReturnListFromEntity() {
        when(Store.listAll(any())).thenReturn(List.of());
        given()
                .when().get("/store")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);

    }

    @Test
    void getStoreById_shouldReturnStoreFromEntity() {
        when(Store.findById(any())).thenReturn(new Store());
        given()
                .when().get("/store/1")
                .then()
                .statusCode(200)
                .contentType(ContentType.JSON);
    }

    @Test
    void createStore_shouldReturn201() {
//        when(Store.persist("id")).thenReturn(any(Store.class));
        given()
                .contentType(ContentType.JSON)
                .body(new Store("store"))
                .when().post("/store")
                .then()
                .statusCode(201);
//        verify(reg, times(1)).registerInterposedSynchronization(any());
    }

    @Test
    void updateStore_shouldReturn200() {
        when(Store.findById(any())).thenReturn(new Store());
        given()
                .contentType(ContentType.JSON)
                .body(new Store("store"))
                .when().put("/store/1")
                .then()
                .statusCode(200);
        verify(legacy, times(1)).updateStoreOnLegacySystem(any());
    }

    @Test
    void patchStore_shouldReturn200() {
        when(Store.findById(any())).thenReturn(new Store());
        given()
                .contentType(ContentType.JSON)
                .body(new Store("store"))
                .when().patch("/store/1")
                .then()
                .statusCode(200);
        verify(legacy, times(1)).updateStoreOnLegacySystem(any());
    }

}
