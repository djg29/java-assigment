package com.fulfilment.application.monolith.warehouses.domain.usecases;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.input.ReplaceWarehouseOperation;
import com.fulfilment.application.monolith.warehouses.domain.ports.output.WarehouseStore;
import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class ReplaceWarehouseUseCase implements ReplaceWarehouseOperation {

  private final WarehouseStore warehouseStore;

  public ReplaceWarehouseUseCase(WarehouseStore warehouseStore) {
    this.warehouseStore = warehouseStore;
  }

  @Override
  public void replace(Warehouse newWarehouse) {
    var existing = warehouseStore.findByBusinessUnitCode(newWarehouse.businessUnitCode);
    if (existing.stock.equals(newWarehouse.stock) && existing.capacity >= newWarehouse.capacity) {
      warehouseStore.update(newWarehouse);
    }
  }
}
