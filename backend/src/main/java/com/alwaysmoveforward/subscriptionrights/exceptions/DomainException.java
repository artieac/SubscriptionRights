package com.alwaysmoveforward.subscriptionrights.exceptions;

/**
 * Thrown when a domain model invariant is violated (bad request, not a server error).
 */
public class DomainException extends RuntimeException {

    public DomainException(String message) {
        super(message);
    }
}
