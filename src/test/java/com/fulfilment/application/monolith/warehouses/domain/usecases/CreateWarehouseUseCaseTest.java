package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.output.WarehouseStore;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.*;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
class CreateWarehouseUseCaseTest {

    @InjectMock
    private WarehouseStore warehouseStore;

    private CreateWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new CreateWarehouseUseCase(warehouseStore);
    }

    /**
     * Helper to create a warehouse with given fields.
     */
    private Warehouse createWarehouse(String businessUnitCode, String locationIdentifier, int capacity) {
        Warehouse w = new Warehouse();
        w.businessUnitCode = businessUnitCode;
        w.setLocation(locationIdentifier);
        w.capacity = capacity;
        return w;
    }

    // --------------------------------------------------------------
    // Happy path: location exists, no existing warehouse, capacity <= maxCapacity
    // --------------------------------------------------------------
    @Test
    void create_shouldPersistWhenLocationExistsAndNoExistingWarehouseAndCapacityWithinLimit() {
        // location "ZWOLLE-001" has maxCapacity = 40
        Warehouse warehouse = createWarehouse("WH001", "ZWOLLE-001", 30);
        when(warehouseStore.findByBusinessUnitCode("WH001")).thenReturn(null);

        useCase.create(warehouse);

        verify(warehouseStore, times(1)).create(warehouse);
    }

    @Test
    void create_shouldPersistWhenCapacityEqualsMaxCapacity() {
        // maxCapacity = 40, capacity = 40 -> allowed (condition is >)
        Warehouse warehouse = createWarehouse("WH002", "ZWOLLE-001", 40);
        when(warehouseStore.findByBusinessUnitCode("WH002")).thenReturn(null);

        useCase.create(warehouse);

        verify(warehouseStore, times(1)).create(warehouse);
    }

    // --------------------------------------------------------------
    // Location exists but capacity exceeds max -> no persist
    // --------------------------------------------------------------
    @Test
    void create_shouldNotPersistWhenCapacityExceedsMaxCapacity() {
        Warehouse warehouse = createWarehouse("WH003", "ZWOLLE-001", 41); // exceeds 40
        when(warehouseStore.findByBusinessUnitCode("WH003")).thenReturn(null);

        useCase.create(warehouse);

        verify(warehouseStore, never()).create(any());
    }

    // --------------------------------------------------------------
    // Location does NOT exist -> no persist
    // --------------------------------------------------------------
    @Test
    void create_shouldNotPersistWhenLocationDoesNotExist() {
        Warehouse warehouse = createWarehouse("WH004", "UNKNOWN-LOCATION", 10);
        when(warehouseStore.findByBusinessUnitCode("WH004")).thenReturn(null);

        useCase.create(warehouse);

        verify(warehouseStore, never()).create(any());
    }

    // --------------------------------------------------------------
    // Warehouse already exists (by businessUnitCode) -> no persist
    // --------------------------------------------------------------
    @Test
    void create_shouldNotPersistWhenWarehouseAlreadyExists() {
        Warehouse existingWarehouse = createWarehouse("WH005", "ZWOLLE-001", 20);
        Warehouse newWarehouse = createWarehouse("WH005", "ZWOLLE-001", 20);
        when(warehouseStore.findByBusinessUnitCode("WH005")).thenReturn(existingWarehouse);

        useCase.create(newWarehouse);

        verify(warehouseStore, never()).create(any());
    }

    // --------------------------------------------------------------
    // Location exists & warehouse not existing, but capacity within limit -> persist
    // (using a different location to be sure)
    // --------------------------------------------------------------
    @Test
    void create_shouldPersistWithDifferentValidLocation() {
        // "AMSTERDAM-001" has maxCapacity = 100
        Warehouse warehouse = createWarehouse("WH006", "AMSTERDAM-001", 99);
        when(warehouseStore.findByBusinessUnitCode("WH006")).thenReturn(null);

        useCase.create(warehouse);

        verify(warehouseStore, times(1)).create(warehouse);
    }

    // --------------------------------------------------------------
    // Edge case: existing == null and location != null, but capacity = 0 -> persist
    // --------------------------------------------------------------
    @Test
    void create_shouldPersistWhenCapacityIsZero() {
        Warehouse warehouse = createWarehouse("WH007", "ZWOLLE-001", 0);
        when(warehouseStore.findByBusinessUnitCode("WH007")).thenReturn(null);

        useCase.create(warehouse);

        verify(warehouseStore, times(1)).create(warehouse);
    }
}
