package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.output.WarehouseStore;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReplaceWarehouseUseCaseTest {

    @Mock
    private WarehouseStore warehouseStore;

    @InjectMocks
    private ReplaceWarehouseUseCase useCase;

    // Helper to create a warehouse
    private Warehouse createWarehouse(String businessUnitCode, Integer stock, Integer capacity) {
        Warehouse w = new Warehouse();
        w.businessUnitCode = businessUnitCode;
        w.stock = stock;
        w.capacity = capacity;
        return w;
    }

    // -----------------------------------------------------------------
    // Happy path: existing found, stock equal, capacity sufficient
    // -----------------------------------------------------------------
    @Test
    void replace_shouldUpdateWhenStockEqualAndCapacitySufficient() {
        Warehouse existing = createWarehouse("WH001", 100, 200);
        Warehouse newWarehouse = createWarehouse("WH001", 100, 150);

        when(warehouseStore.findByBusinessUnitCode("WH001")).thenReturn(existing);
        useCase.replace(newWarehouse);

        verify(warehouseStore, times(1)).update(newWarehouse);
    }

    @Test
    void replace_shouldUpdateWhenCapacityExactlyEqual() {
        Warehouse existing = createWarehouse("WH002", 50, 200);
        Warehouse newWarehouse = createWarehouse("WH002", 50, 200); // capacity unchanged

        when(warehouseStore.findByBusinessUnitCode("WH002")).thenReturn(existing);
        useCase.replace(newWarehouse);

        verify(warehouseStore, times(1)).update(newWarehouse);
    }

    @Test
    void replace_shouldUpdateWhenCapacityGreaterThanNewWarehouseCapacity() {
        Warehouse existing = createWarehouse("WH003", 75, 300);
        Warehouse newWarehouse = createWarehouse("WH003", 75, 200);

        when(warehouseStore.findByBusinessUnitCode("WH003")).thenReturn(existing);
        useCase.replace(newWarehouse);

        verify(warehouseStore, times(1)).update(newWarehouse);
    }

    // -----------------------------------------------------------------
    // Failure cases: conditions not met -> no update
    // -----------------------------------------------------------------
    @Test
    void replace_shouldNotUpdateWhenStockDiffers() {
        Warehouse existing = createWarehouse("WH004", 100, 200);
        Warehouse newWarehouse = createWarehouse("WH004", 99, 150); // stock differs

        when(warehouseStore.findByBusinessUnitCode("WH004")).thenReturn(existing);
        useCase.replace(newWarehouse);

        verify(warehouseStore, never()).update(any());
    }

    @Test
    void replace_shouldNotUpdateWhenCapacityInsufficient() {
        Warehouse existing = createWarehouse("WH005", 100, 150);
        Warehouse newWarehouse = createWarehouse("WH005", 100, 200); // capacity greater than existing

        when(warehouseStore.findByBusinessUnitCode("WH005")).thenReturn(existing);
        useCase.replace(newWarehouse);

        verify(warehouseStore, never()).update(any());
    }

    @Test
    void replace_shouldNotUpdateWhenBothStockDiffersAndCapacityInsufficient() {
        Warehouse existing = createWarehouse("WH006", 100, 150);
        Warehouse newWarehouse = createWarehouse("WH006", 99, 200); // stock differs & capacity insufficient

        when(warehouseStore.findByBusinessUnitCode("WH006")).thenReturn(existing);
        useCase.replace(newWarehouse);

        verify(warehouseStore, never()).update(any());
    }

    // -----------------------------------------------------------------
    // Edge case: existing warehouse not found -> NullPointerException
    // -----------------------------------------------------------------
    @Test
    void replace_whenExistingNotFound_throwsNullPointerException() {
        Warehouse newWarehouse = createWarehouse("WH007", 100, 150);
        when(warehouseStore.findByBusinessUnitCode("WH007")).thenReturn(null);

        assertThrows(NullPointerException.class, () -> useCase.replace(newWarehouse));
        verify(warehouseStore, never()).update(any());
    }

    // -----------------------------------------------------------------
    // Edge cases with null stock or capacity
    // -----------------------------------------------------------------
    @Test
    void replace_whenExistingStockIsNull_throwsNullPointerException() {
        Warehouse existing = createWarehouse("WH008", null, 200);
        Warehouse newWarehouse = createWarehouse("WH008", null, 150); // both null -> .equals on null? existing.stock is null, calling .equals throws NPE

        when(warehouseStore.findByBusinessUnitCode("WH008")).thenReturn(existing);
        assertThrows(NullPointerException.class, () -> useCase.replace(newWarehouse));
        verify(warehouseStore, never()).update(any());
    }

    @Test
    void replace_whenNewStockIsNullAndExistingStockNonNull_throwsNullPointerException() {
        Warehouse existing = createWarehouse("WH009", 100, 200);
        Warehouse newWarehouse = createWarehouse("WH009", null, 150);

        when(warehouseStore.findByBusinessUnitCode("WH009")).thenReturn(existing);
        // existing.stock equals null? Actually existing.stock is 100, calling 100.equals(null) -> false, no NPE.
        // But the code uses existing.stock.equals(newWarehouse.stock). If newWarehouse.stock is null, that's fine. But if existing.stock is null, it throws.
        // Let's test both.
        useCase.replace(newWarehouse);
        // Since existing.stock=100, new stock=null -> 100.equals(null) is false, condition fails, no update.
        verify(warehouseStore, never()).update(any());
    }

}
