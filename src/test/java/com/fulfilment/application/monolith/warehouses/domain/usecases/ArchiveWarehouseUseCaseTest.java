package com.fulfilment.application.monolith.warehouses.domain.usecases;


import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.output.WarehouseStore;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
public class ArchiveWarehouseUseCaseTest {
    @InjectMock
    private WarehouseStore warehouseStore;

    private ArchiveWarehouseUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new ArchiveWarehouseUseCase(warehouseStore);
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

    @Test
    void archive_shouldUpdateWarehouse() {
        // location "ZWOLLE-001" has maxCapacity = 40
        Warehouse warehouse = createWarehouse("WH001", "ZWOLLE-001", 30);
        useCase.archive(warehouse);

        verify(warehouseStore, times(1)).update(warehouse);
    }

}
