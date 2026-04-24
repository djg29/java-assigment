package com.fulfilment.application.monolith.fulfillment.service;

import com.fulfilment.application.monolith.fulfillment.dao.FulfillmentRepo;
import com.fulfilment.application.monolith.fulfillment.dao.entity.FulfillmentAssignment;
import com.fulfilment.application.monolith.fulfillment.model.Fulfillment;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FulFillmentServiceImplTest {

    @Mock
    private FulfillmentRepo repo;

    @InjectMocks
    private FulFillmentServiceImpl service;

    // Helper to create a test assignment
    private FulfillmentAssignment createAssignment(String businessUnit, String store, String product) {
        FulfillmentAssignment a = new FulfillmentAssignment();
        a.warehouseBusinessUnitCode = businessUnit;
        a.storeName = store;
        a.productName = product;
        return a;
    }

    // Helper to create a test model
    private Fulfillment createFulfillment(String businessUnit, String store, String product) {
        return new Fulfillment(businessUnit, product, store);
    }

    @Test
    void listAllFulfillments_shouldReturnAllAsModels() {
        List<FulfillmentAssignment> assignments = List.of(
                createAssignment("WH1", "StoreA", "ProductX"),
                createAssignment("WH2", "StoreB", "ProductY")
        );
        when(repo.listAll()).thenReturn(assignments);

        List<Fulfillment> result = service.listAllFulfillments();

        assertEquals(2, result.size());
        assertEquals("WH1", result.get(0).businessUnitCode);
        assertEquals("ProductX", result.get(0).productName);
        assertEquals("StoreA", result.get(0).storeName);
        assertEquals("WH2", result.get(1).businessUnitCode);
        verify(repo, times(1)).listAll();
    }

    @Test
    void listAllFulfillments_whenEmpty_returnsEmptyList() {
        when(repo.listAll()).thenReturn(List.of());
        List<Fulfillment> result = service.listAllFulfillments();
        assertTrue(result.isEmpty());
    }

    @Test
    void findByWarehouseProductAndStore_shouldReturnMappedModels() {
        List<FulfillmentAssignment> assignments = List.of(createAssignment("WH1", "StoreA", "ProductX"));
        when(repo.findByWarehouseBusinessUnitCodeAndStoreNameAndProductName("WH1", "StoreA", "ProductX"))
                .thenReturn(assignments);

        List<Fulfillment> result = service.findByWarehouseProductAndStore("WH1", "StoreA", "ProductX");

        assertEquals(1, result.size());
        assertEquals("WH1", result.get(0).businessUnitCode);
        verify(repo, times(1)).findByWarehouseBusinessUnitCodeAndStoreNameAndProductName("WH1", "StoreA", "ProductX");
    }

    @Test
    void findByWarehouse_shouldReturnMappedModels() {
        List<FulfillmentAssignment> assignments = List.of(createAssignment("WH1", "StoreA", "ProductX"));
        when(repo.findByWarehouseBusinessUnitCode("WH1")).thenReturn(assignments);

        List<Fulfillment> result = service.findByWarehouse("WH1");

        assertEquals(1, result.size());
        assertEquals("WH1", result.get(0).businessUnitCode);
    }

    @Test
    void findByProductAndStore_shouldReturnMappedModels() {
        List<FulfillmentAssignment> assignments = List.of(createAssignment("WH1", "StoreA", "ProductX"));
        when(repo.findByStoreNameAndProductName("StoreA", "ProductX")).thenReturn(assignments);

        List<Fulfillment> result = service.findByProductAndStore("StoreA", "ProductX");

        assertEquals(1, result.size());
        verify(repo).findByStoreNameAndProductName("StoreA", "ProductX");
    }

    @Test
    void findByStore_shouldReturnMappedModels() {
        List<FulfillmentAssignment> assignments = List.of(createAssignment("WH1", "StoreA", "ProductX"));
        when(repo.findByStoreName("StoreA")).thenReturn(assignments);

        List<Fulfillment> result = service.findByStore("StoreA");

        assertEquals(1, result.size());
        verify(repo).findByStoreName("StoreA");
    }

    // ---------- createFulfillment business logic tests ----------

    @Test
    void createFulfillment_whenAssignmentAlreadyExists_throwsException() {
        Fulfillment fulfillment = createFulfillment("WH1", "StoreA", "ProductX");
        when(repo.findByWarehouseBusinessUnitCodeAndStoreNameAndProductName("WH1", "StoreA", "ProductX"))
                .thenReturn(List.of(createAssignment("WH1", "StoreA", "ProductX"))); // exists

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.createFulfillment(fulfillment));
        assertEquals("Assignment already exists", ex.getMessage());
        verify(repo, never()).persist((FulfillmentAssignment) any());
    }

    @Test
    void createFulfillment_whenProductAlreadyAssignedToTwoWarehousesInStore_throwsException() {
        Fulfillment fulfillment = createFulfillment("WH3", "StoreA", "ProductX");
        // No exact match for (WH3,StoreA,ProductX)
        when(repo.findByWarehouseBusinessUnitCodeAndStoreNameAndProductName("WH3", "StoreA", "ProductX"))
                .thenReturn(List.of());
        // Already two warehouses for the same product & store
        when(repo.findByStoreNameAndProductName("StoreA", "ProductX"))
                .thenReturn(List.of(
                        createAssignment("WH1", "StoreA", "ProductX"),
                        createAssignment("WH2", "StoreA", "ProductX")
                ));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.createFulfillment(fulfillment));
        assertEquals("Product in store has maxed out", ex.getMessage());
        verify(repo, never()).persist((FulfillmentAssignment) any());
    }

    @Test
    void createFulfillment_whenStoreAlreadyHasThreeWarehouses_throwsException() {
        Fulfillment fulfillment = createFulfillment("WH4", "StoreA", "ProductX");
        // No exact duplicate
        when(repo.findByWarehouseBusinessUnitCodeAndStoreNameAndProductName("WH4", "StoreA", "ProductX"))
                .thenReturn(List.of());
        // Less than 2 assignments for product+store (so product limit passes)
        when(repo.findByStoreNameAndProductName("StoreA", "ProductX"))
                .thenReturn(List.of(createAssignment("WH1", "StoreA", "ProductX"))); // only 1
        // But store already has 3 distinct warehouses
        when(repo.countDistinctWarehousesByStore("StoreA")).thenReturn(3L);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.createFulfillment(fulfillment));
        assertEquals("Store already has max warehouses assigned to it", ex.getMessage());
        verify(repo, never()).persist((FulfillmentAssignment) any());
    }

    @Test
    void createFulfillment_whenAllConditionsMet_persistsAndReturnsFulfillment() {
        Fulfillment fulfillment = createFulfillment("WH1", "StoreA", "ProductX");
        // No existing exact match
        when(repo.findByWarehouseBusinessUnitCodeAndStoreNameAndProductName("WH1", "StoreA", "ProductX"))
                .thenReturn(List.of());
        // Product+store count less than 2
        when(repo.findByStoreNameAndProductName("StoreA", "ProductX"))
                .thenReturn(List.of()); // zero
        // Store warehouse distinct count less than 3
        when(repo.countDistinctWarehousesByStore("StoreA")).thenReturn(2L);

        Fulfillment result = service.createFulfillment(fulfillment);

        assertSame(fulfillment, result);
        verify(repo, times(1)).persist(any(FulfillmentAssignment.class));
    }

    @Test
    void createFulfillment_whenStoreWarehouseCountExactlyThree_throws() {
        Fulfillment fulfillment = createFulfillment("WH4", "StoreB", "ProductY");
        when(repo.findByWarehouseBusinessUnitCodeAndStoreNameAndProductName("WH4", "StoreB", "ProductY"))
                .thenReturn(List.of());
        when(repo.findByStoreNameAndProductName("StoreB", "ProductY"))
                .thenReturn(List.of()); // product limit not hit
        when(repo.countDistinctWarehousesByStore("StoreB")).thenReturn(3L);

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.createFulfillment(fulfillment));
        assertEquals("Store already has max warehouses assigned to it", ex.getMessage());
    }

    @Test
    void createFulfillment_whenProductInStoreAlreadyHasTwoWarehouses_throws() {
        Fulfillment fulfillment = createFulfillment("WH3", "StoreC", "ProductZ");
        when(repo.findByWarehouseBusinessUnitCodeAndStoreNameAndProductName("WH3", "StoreC", "ProductZ"))
                .thenReturn(List.of());
        when(repo.findByStoreNameAndProductName("StoreC", "ProductZ"))
                .thenReturn(List.of(
                        createAssignment("WH1", "StoreC", "ProductZ"),
                        createAssignment("WH2", "StoreC", "ProductZ")
                ));

        RuntimeException ex = assertThrows(RuntimeException.class, () -> service.createFulfillment(fulfillment));
        assertEquals("Product in store has maxed out", ex.getMessage());
    }
}
