package com.fulfilment.application.monolith.warehouses.domain.ports.input;

import com.fulfilment.application.monolith.warehouses.domain.models.Location;

public interface LocationResolver {
  Location resolveByIdentifier(String identifier);
}
