package com.fulfilment.application.monolith.fulfillment.controller;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fulfilment.application.monolith.fulfillment.FullfilmentFailureException;
import com.fulfilment.application.monolith.fulfillment.model.Fulfillment;
import com.fulfilment.application.monolith.fulfillment.service.FulFillmentService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Response;
import java.util.List;

import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.jboss.logging.Logger;

@Path("fulfillment")
@ApplicationScoped
@Produces("application/json")
@Consumes("application/json")
public class FulfillmentController {

    private static final Logger LOGGER = Logger.getLogger(FulfillmentController.class.getName());

    @Inject FulFillmentService service;
    @Inject
    Validator validator;

    @GET
    public List<Fulfillment> getAllfulfillments() {
        LOGGER.info("get all available fulfillments");
        var lst = service.listAllFulfillments();
        LOGGER.infof("returned a list with %d fulfillments", lst.size());
        return lst;
    }

    @POST
    @Transactional
    public Response create(@Valid Fulfillment fulfillment) {
        LOGGER.infof("create fulfillment with %s", fulfillment);
        service.createFulfillment(fulfillment);
        LOGGER.info("successfully created fulfillment");
        return Response.ok(fulfillment).status(201).build();
    }

    @Provider
    public static class ErrorMapper implements ExceptionMapper<RuntimeException> {

        @Inject ObjectMapper objectMapper;

        @Override
        public Response toResponse(RuntimeException exception) {
            LOGGER.error("Failed to handle request", exception);

            int code = 500;
            if (exception instanceof FullfilmentFailureException) {
                code = 400;
            } else if (exception instanceof WebApplicationException) {
                code = ((WebApplicationException) exception).getResponse().getStatus();
            }

            ObjectNode exceptionJson = objectMapper.createObjectNode();
            exceptionJson.put("exceptionType", exception.getClass().getName());
            exceptionJson.put("code", code);

            if (exception.getMessage() != null) {
                exceptionJson.put("error", exception.getMessage());
            }

            return Response.status(code).entity(exceptionJson).build();
        }

    }
}
