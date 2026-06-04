package com.fulfilment.application.monolith.warehouses.domain.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class Warehouse {

  @NotBlank(message = "businessUnitCode cannot be empty")
  public String businessUnitCode;

  @NotBlank(message = "location cannot be empty")
  public String location;

  @NotNull(message = "capacity is required")
  public Integer capacity;

  @NotNull(message = "stock is required")
  public Integer stock;

  public LocalDateTime createdAt;

  public LocalDateTime archivedAt;

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

    @Override
    public String toString() {
        return "Warehouse{" +
                "businessUnitCode='" + businessUnitCode + '\'' +
                ", location='" + location + '\'' +
                ", capacity=" + capacity +
                ", stock=" + stock +
                ", createdAt=" + createdAt +
                ", archivedAt=" + archivedAt +
                '}';
    }
}
