package com.fulfilment.application.monolith.warehouses.domain.ports.input;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

public interface CreateWarehouseOperation {
  void create(Warehouse warehouse);
}
