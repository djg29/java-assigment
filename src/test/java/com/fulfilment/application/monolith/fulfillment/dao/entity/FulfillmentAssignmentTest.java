package com.fulfilment.application.monolith.fulfillment.dao.entity;

import com.fulfilment.application.monolith.fulfillment.model.Fulfillment;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class FulfillmentAssignmentTest {

    @Test
    void toFulfillment_shouldMapAllFieldsCorrectly() {
        // Given
        FulfillmentAssignment assignment = new FulfillmentAssignment();
        assignment.warehouseBusinessUnitCode = "WH-001";
        assignment.storeName = "Test Store";
        assignment.productName = "Test Product";

        // When
        Fulfillment fulfillment = assignment.toFulfillment();

        // Then
        assertNotNull(fulfillment);
        assertEquals(assignment.warehouseBusinessUnitCode, fulfillment.businessUnitCode);
        assertEquals(assignment.productName, fulfillment.productName);
        assertEquals(assignment.storeName, fulfillment.storeName);
    }

    @Test
    void toFulfillment_whenFieldsAreNull_shouldMapNulls() {
        // Given
        FulfillmentAssignment assignment = new FulfillmentAssignment();
        // All fields remain null

        // When
        Fulfillment fulfillment = assignment.toFulfillment();

        // Then
        assertNotNull(fulfillment);
        assertNull(fulfillment.businessUnitCode);
        assertNull(fulfillment.productName);
        assertNull(fulfillment.storeName);
    }

    @Test
    void fromFulfillment_shouldMapAllFieldsCorrectly() {
        // Given
        Fulfillment fulfillment = new Fulfillment("WH-001", "Test Product", "Test Store");

        // When
        FulfillmentAssignment assignment = FulfillmentAssignment.fromFulfillment(fulfillment);

        // Then
        assertNotNull(assignment);
        assertEquals(fulfillment.businessUnitCode, assignment.warehouseBusinessUnitCode);
        assertEquals(fulfillment.productName, assignment.productName);
        assertEquals(fulfillment.storeName, assignment.storeName);
    }

    @Test
    void fromFulfillment_whenFulfillmentFieldsAreNull_shouldMapNulls() {
        // Given
        Fulfillment fulfillment = new Fulfillment(null, null, null);

        // When
        FulfillmentAssignment assignment = FulfillmentAssignment.fromFulfillment(fulfillment);

        // Then
        assertNotNull(assignment);
        assertNull(assignment.warehouseBusinessUnitCode);
        assertNull(assignment.productName);
        assertNull(assignment.storeName);
    }

    @Test
    void fromFulfillment_toFulfillment_shouldBeIdempotent() {
        // Given
        Fulfillment original = new Fulfillment("WH-001", "Test Product", "Test Store");

        // When
        FulfillmentAssignment assignment = FulfillmentAssignment.fromFulfillment(original);
        Fulfillment result = assignment.toFulfillment();

        // Then
        assertEquals(original.businessUnitCode, result.businessUnitCode);
        assertEquals(original.productName, result.productName);
        assertEquals(original.storeName, result.storeName);
    }
}
