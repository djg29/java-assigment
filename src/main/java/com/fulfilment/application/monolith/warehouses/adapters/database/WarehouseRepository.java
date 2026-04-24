package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import com.fulfilment.application.monolith.warehouses.domain.ports.output.WarehouseStore;
import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;

@ApplicationScoped
public class WarehouseRepository implements WarehouseStore, PanacheRepository<DbWarehouse> {

  @Override
  public List<Warehouse> getAll() {
    return this.listAll().stream().map(DbWarehouse::toWarehouse).toList();
  }

  @Override
  public void create(Warehouse warehouse) {
    DbWarehouse entity = DbWarehouse.fromWarehouse(warehouse);
    this.persist(entity);
  }

  @Override
  public void update(Warehouse warehouse) {
    find("businessUnitCode", warehouse.getBusinessUnitCode()).firstResultOptional().ifPresentOrElse(entity -> {
      entity.setBusinessUnitCode(warehouse.businessUnitCode);
      entity.setCapacity(warehouse.capacity);
      entity.setLocation(warehouse.location);
      entity.setStock(warehouse.stock);
      this.persist(entity);
    },
            () -> {
                throw new RuntimeException("Warehouse not found for business unit code: " + warehouse.businessUnitCode);
            });
  }

  @Override
  public void remove(Warehouse warehouse) {
    long res = delete("businessUnitCode", warehouse.getBusinessUnitCode());
  }

  @Override
  public Warehouse findByBusinessUnitCode(String buCode) {
    var entity = find("businessUnitCode", buCode).firstResultOptional().map(DbWarehouse::toWarehouse);
    return entity.orElse(null);
  }
}
