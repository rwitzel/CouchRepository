package com.github.rwitzel.couchrepository.api.exceptions;

import java.util.List;

/**
 * Thrown if an error happens during a bulk operation.
 * 
 * @author rwitzel
 */
public class BulkOperationException extends RuntimeException {

    private static final long serialVersionUID = 8946510245961827774L;

    private List<BulkOperationError> errors;

    public BulkOperationException(String message, List<BulkOperationError> errors) {
        super(message);
        this.errors = errors;
    }

    public List<BulkOperationError> getErrors() {
        return errors;
    }

    @Override
    public String toString() {
        return "BulkOperationException [errors=" + errors + ", getMessage()=" + getMessage() + "]";
    }

}
