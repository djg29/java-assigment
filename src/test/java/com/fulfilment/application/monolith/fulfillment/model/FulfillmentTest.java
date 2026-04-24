package com.fulfilment.application.monolith.fulfillment.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FulfillmentTest {

    @Test
    void constructor_shouldSetFieldsCorrectly() {
        String businessCode = "WH-001";
        String product = "Laptop";
        String store = "Amsterdam Store";

        Fulfillment fulfillment = new Fulfillment(businessCode, product, store);

        assertEquals(businessCode, fulfillment.businessUnitCode);
        assertEquals(product, fulfillment.productName);
        assertEquals(store, fulfillment.storeName);

        // Also verify via getters
        assertEquals(businessCode, fulfillment.getBusinessUntiCode());
        assertEquals(product, fulfillment.getProductName());
        assertEquals(store, fulfillment.getStoreName());
    }

    @Test
    void constructor_withNullValues_shouldAllowNull() {
        Fulfillment fulfillment = new Fulfillment(null, null, null);
        assertNull(fulfillment.businessUnitCode);
        assertNull(fulfillment.productName);
        assertNull(fulfillment.storeName);
    }

    @Test
    void setterAndGetter_shouldUpdateAndReturnCorrectValue() {
        Fulfillment fulfillment = new Fulfillment("", "", "");

        fulfillment.setBusinessUntiCode("WH-002");
        assertEquals("WH-002", fulfillment.getBusinessUntiCode());

        fulfillment.setProductName("Monitor");
        assertEquals("Monitor", fulfillment.getProductName());

        fulfillment.setStoreName("Rotterdam Store");
        assertEquals("Rotterdam Store", fulfillment.getStoreName());
    }

    @Test
    void setter_withNull_shouldSetNull() {
        Fulfillment fulfillment = new Fulfillment("WH-001", "Product", "Store");
        fulfillment.setBusinessUntiCode(null);
        fulfillment.setProductName(null);
        fulfillment.setStoreName(null);

        assertNull(fulfillment.getBusinessUntiCode());
        assertNull(fulfillment.getProductName());
        assertNull(fulfillment.getStoreName());
    }

    @Test
    void directFieldAccess_shouldReflectSetterChanges() {
        Fulfillment fulfillment = new Fulfillment("WH-001", "Mouse", "Utrecht");
        fulfillment.setBusinessUntiCode("WH-999");
        assertEquals("WH-999", fulfillment.businessUnitCode);
    }
}
