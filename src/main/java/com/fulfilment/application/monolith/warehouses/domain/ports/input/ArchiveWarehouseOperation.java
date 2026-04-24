package com.fulfilment.application.monolith.warehouses.domain.ports.input;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;

public interface ArchiveWarehouseOperation {
  void archive(Warehouse warehouse);
}
