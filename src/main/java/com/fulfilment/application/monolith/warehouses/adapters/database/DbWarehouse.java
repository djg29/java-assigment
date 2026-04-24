package com.fulfilment.application.monolith.warehouses.adapters.database;

import com.fulfilment.application.monolith.warehouses.domain.models.Warehouse;
import jakarta.persistence.Cacheable;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "warehouse")
@Cacheable
public class DbWarehouse {

  @Id @GeneratedValue public Long id;

  public String businessUnitCode;

  public String location;

  public Integer capacity;

  public Integer stock;

  public LocalDateTime createdAt;

  public LocalDateTime archivedAt;

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getBusinessUnitCode() {
    return businessUnitCode;
  }

  public void setBusinessUnitCode(String businessUnitCode) {
    this.businessUnitCode = businessUnitCode;
  }

  public String getLocation() {
    return location;
  }

  public void setLocation(String location) {
    this.location = location;
  }

  public Integer getCapacity() {
    return capacity;
  }

  public void setCapacity(Integer capacity) {
    this.capacity = capacity;
  }

  public Integer getStock() {
    return stock;
  }

  public void setStock(Integer stock) {
    this.stock = stock;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getArchivedAt() {
    return archivedAt;
  }

  public void setArchivedAt(LocalDateTime archivedAt) {
    this.archivedAt = archivedAt;
  }

  public DbWarehouse() {}

  public DbWarehouse(String businessUnitCode, String location, Integer capacity, Integer stock, LocalDateTime createdAt, LocalDateTime archivedAt) {
    this.businessUnitCode = businessUnitCode;
    this.location = location;
    this.capacity = capacity;
    this.stock = stock;
    this.createdAt = createdAt;
    this.archivedAt = archivedAt;
  }

  public Warehouse toWarehouse() {
    var warehouse = new Warehouse();
    warehouse.businessUnitCode = this.businessUnitCode;
    warehouse.location = this.location;
    warehouse.capacity = this.capacity;
    warehouse.stock = this.stock;
    warehouse.createdAt = this.createdAt;
    warehouse.archivedAt = this.archivedAt;
    return warehouse;
  }

  public static DbWarehouse fromWarehouse(Warehouse warehouse) {
    return new DbWarehouse(
            warehouse.businessUnitCode,
            warehouse.location,
            warehouse.capacity,
            warehouse.stock,
            warehouse.createdAt,
            warehouse.archivedAt
    );
  }
}
