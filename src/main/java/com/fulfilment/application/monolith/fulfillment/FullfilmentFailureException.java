package com.fulfilment.application.monolith.fulfillment;

public class FullfilmentFailureException extends RuntimeException {
    public FullfilmentFailureException(String message) {
        super(message);
    }
}
