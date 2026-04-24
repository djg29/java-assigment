package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.location.LocationGateway;
import com.fulfilment.application.monolith.warehouses.domain.models.Location;
import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.input.CreateWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.output.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class CreateWarehouseUseCase implements CreateWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public CreateWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void create(Warehouse warehouse) {
    var lg = new LocationGateway();
    var loc = lg.resolveByIdentifier(warehouse.getLocation());
    var existing = warehouseStore.findByBusinessUnitCode(warehouse.businessUnitCode);
    if (loc != null & existing == null) {
      var maxCapacityExceeded = warehouse.capacity > loc.maxCapacity;
      if (!maxCapacityExceeded) {
        warehouseStore.create(warehouse);
      }
    }
  }
}
