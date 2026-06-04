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

}
