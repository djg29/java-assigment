package com.fulfilment.application.monolith.warehouses.adapters.restapi;

import com.fulfilment.application.monolith.warehouses.adapters.database.WarehouseRepository;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.input.ArchiveWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.input.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.input.ReplaceWarehouseOperation;
import io.quarkus.test.InjectMock;
import io.quarkus.test.junit.QuarkusTest;
import jakarta.inject.Inject;
import jakarta.ws.rs.WebApplicationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@QuarkusTest
@ExtendWith(MockitoExtension.class)
class WarehouseResourceImplTest {

    @InjectMock
    private WarehouseRepository warehouseRepository;

    @InjectMock
    private ArchiveWarehouseOperation archiveWarehouseOperation;

    @InjectMock
    private CreateWarehouseOperation createWarehouseOperation;

    @InjectMock
    private ReplaceWarehouseOperation replaceWarehouseOperation;

    @Inject
    private WarehouseResourceImpl resource;

    // Helper to create a domain Warehouse
    private Warehouse createDomainWarehouse(String code, String location, int capacity, int stock) {
        Warehouse w = new Warehouse();
        w.businessUnitCode = code;
        w.location = location;
        w.capacity = capacity;
        w.stock = stock;
        return w;
    }

    // Helper to create an API Warehouse (same class as domain for simplicity)
    private Warehouse createApiWarehouse(String code, String location, int capacity, int stock) {
        Warehouse w = new Warehouse();
        w.setBusinessUnitCode(code);
        w.setLocation(location);
        w.setCapacity(capacity);
        w.setStock(stock);
        return w;
    }

    @Test
    void listAllWarehousesUnits_shouldReturnAllMappedWarehouses() {
        // Given
        Warehouse domain1 = createDomainWarehouse("WH1", "Loc1", 100, 50);
        Warehouse domain2 = createDomainWarehouse("WH2", "Loc2", 200, 80);
        when(warehouseRepository.getAll()).thenReturn(List.of(domain1, domain2));

        // When
        List<Warehouse> result = resource.listAllWarehousesUnits();

        // Then
        assertEquals(2, result.size());
        Warehouse api1 = result.get(0);
        assertEquals("WH1", api1.getBusinessUnitCode());
        assertEquals("Loc1", api1.getLocation());
        assertEquals(100, api1.getCapacity());
        assertEquals(50, api1.getStock());

        Warehouse api2 = result.get(1);
        assertEquals("WH2", api2.getBusinessUnitCode());
        assertEquals("Loc2", api2.getLocation());
        assertEquals(200, api2.getCapacity());
        assertEquals(80, api2.getStock());

        verify(warehouseRepository, times(1)).getAll();
    }

    @Test
    void listAllWarehousesUnits_whenRepositoryEmpty_shouldReturnEmptyList() {
        when(warehouseRepository.getAll()).thenReturn(List.of());

        List<Warehouse> result = resource.listAllWarehousesUnits();

        assertTrue(result.isEmpty());
        verify(warehouseRepository).getAll();
    }

    @Test
    void createANewWarehouseUnit_shouldCallCreateOperationAndReturnData() {
        Warehouse data = createApiWarehouse("WH3", "Loc3", 150, 30);

        Warehouse result = resource.createANewWarehouseUnit(data);

        assertSame(data, result);
        verify(createWarehouseOperation, times(1)).create(data);
    }

    @Test
    void getAWarehouseUnitByID_whenExists_shouldReturnWarehouse() {
        Warehouse domain = createDomainWarehouse("WH4", "Loc4", 120, 40);
        when(warehouseRepository.findByBusinessUnitCode("WH4")).thenReturn(domain);

        Warehouse result = resource.getAWarehouseUnitByID("WH4");

        assertSame(domain, result);  // note: returned domain object directly; if API expects mapping, adjust accordingly
        verify(warehouseRepository).findByBusinessUnitCode("WH4");
    }

    @Test
    void getAWarehouseUnitByID_whenNotExists_shouldReturnNull() {
        when(warehouseRepository.findByBusinessUnitCode("UNKNOWN")).thenReturn(null);

        WebApplicationException exception = assertThrows(WebApplicationException.class, () -> {
            resource.getAWarehouseUnitByID("UNKNOWN");
        });

        assertEquals(404, exception.getResponse().getStatus());
        verify(warehouseRepository).findByBusinessUnitCode("UNKNOWN");
    }

    @Test
    void archiveAWarehouseUnitByID_whenWarehouseExists_shouldCallArchiveOperation() {
        Warehouse domain = createDomainWarehouse("WH5", "Loc5", 100, 10);
        when(warehouseRepository.findByBusinessUnitCode("WH5")).thenReturn(domain);

        resource.archiveAWarehouseUnitByID("WH5");

        verify(archiveWarehouseOperation, times(1)).archive(domain);
        verify(warehouseRepository).findByBusinessUnitCode("WH5");
    }

    @Test
    void archiveAWarehouseUnitByID_whenWarehouseNotExists_shouldDoNothing() {
        when(warehouseRepository.findByBusinessUnitCode("MISSING")).thenReturn(null);

        resource.archiveAWarehouseUnitByID("MISSING");

        verify(archiveWarehouseOperation, never()).archive(any());
        verify(warehouseRepository).findByBusinessUnitCode("MISSING");
    }

    @Test
    void replaceTheCurrentActiveWarehouse_shouldCallReplaceOperationAndReturnData() {
        Warehouse data = createApiWarehouse("WH6", "Loc6", 300, 90);

        Warehouse result = resource.replaceTheCurrentActiveWarehouse("WH6", data);

        assertSame(data, result);
        verify(replaceWarehouseOperation, times(1)).replace(data);
    }
}
