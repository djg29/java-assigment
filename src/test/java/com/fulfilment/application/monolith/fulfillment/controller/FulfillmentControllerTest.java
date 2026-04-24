package com.fulfilment.application.monolith.fulfillment.controller;

import com.fulfilment.application.monolith.fulfillment.model.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.service.FulFillmentService;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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

        // When
        List<Fulfillment> result = controller.getAllfulfillments();

        // Then
        assertSame(expectedList, result);
        verify(service, times(1)).listAllFulfillments();
    }

    @Test
    void getAllfulfillments_whenServiceReturnsEmptyList_shouldReturnEmptyList() {
        // Given
        List<Fulfillment> emptyList = List.of();
        when(service.listAllFulfillments()).thenReturn(emptyList);

        // When
        List<Fulfillment> result = controller.getAllfulfillments();

        // Then
        assertTrue(result.isEmpty());
        verify(service, times(1)).listAllFulfillments();
    }

    @Test
    void create_shouldCallServiceAndReturnResponseWithStatus201() {
        // Given
        Fulfillment fulfillment = new Fulfillment("code", "product", "store");

        // When
        Response response = controller.create(fulfillment);

        // Then
        verify(service, times(1)).createFulfillment(fulfillment);
        assertEquals(201, response.getStatus());
        assertEquals(fulfillment, response.getEntity());
    }

    @Test
    void create_whenServiceThrowsException_shouldPropagateException() {
        // Given
        Fulfillment fulfillment = new Fulfillment("code", "product", "store");
        doThrow(new RuntimeException("DB error")).when(service).createFulfillment(fulfillment);

        // When / Then
        assertThrows(RuntimeException.class, () -> controller.create(fulfillment));
        verify(service, times(1)).createFulfillment(fulfillment);
    }
}