package com.fulfilment.application.monolith.fulfillment.service;

import com.fulfilment.application.monolith.fulfillment.dao.entity.FulfillmentAssignment;
import com.fulfilment.application.monolith.fulfillment.model.Fulfillment;
import java.util.List;

public interface FulFillmentService {

    List<Fulfillment> listAllFulfillments();

    Fulfillment createFulfillment(Fulfillment fl);

}
