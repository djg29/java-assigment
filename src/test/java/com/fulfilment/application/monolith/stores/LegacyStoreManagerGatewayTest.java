package com.fulfilment.application.monolith.stores;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class LegacyStoreManagerGatewayTest {

    private final LegacyStoreManagerGateway gateway = new LegacyStoreManagerGateway();

    @Test
    void createStoreOnLegacySystem_shouldNotThrowException() {
        Store store = new Store();
        store.name = "TestStore";
        store.quantityProductsInStock = 42;

        assertDoesNotThrow(() -> gateway.createStoreOnLegacySystem(store));
    }

    @Test
    void updateStoreOnLegacySystem_shouldNotThrowException() {
        Store store = new Store();
        store.name = "UpdateStore";
        store.quantityProductsInStock = 100;

        assertDoesNotThrow(() -> gateway.updateStoreOnLegacySystem(store));
    }

    @Test
    void writeToFile_shouldCreateAndDeleteTempFileSuccessfully(@TempDir Path tempDir) {
        // This test verifies the file operations by capturing System.out.
        // We can't directly access the private method, but we can call a public method
        // and verify that the output messages indicate success.

        Store store = new Store();
        store.name = "CaptureStore";
        store.quantityProductsInStock = 7;

        // Redirect System.out to capture logs
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;
        System.setOut(new PrintStream(outContent));

        try {
            gateway.createStoreOnLegacySystem(store);
            String output = outContent.toString();

            assertTrue(output.contains("Temporary file created at:"));
            assertTrue(output.contains("Data written to temporary file."));
            assertTrue(output.contains("Data read from temporary file:"));
            assertTrue(output.contains("Store created. [ name =" + store.name));
            assertTrue(output.contains("items on stock =" + store.quantityProductsInStock));
            assertTrue(output.contains("Temporary file deleted."));
        } finally {
            System.setOut(originalOut);
        }
    }

    @Test
    void writeToFile_withEmptyStoreName_stillCreatesTempFile() {
        Store store = new Store();
        store.name = "";
        store.quantityProductsInStock = 0;

        assertDoesNotThrow(() -> gateway.createStoreOnLegacySystem(store));
    }

    @Test
    void writeToFile_withNullStoreName_shouldHandleGracefully() {
        Store store = new Store();
        store.name = null;
        store.quantityProductsInStock = 5;

        assertDoesNotThrow(() -> gateway.createStoreOnLegacySystem(store));
    }
}