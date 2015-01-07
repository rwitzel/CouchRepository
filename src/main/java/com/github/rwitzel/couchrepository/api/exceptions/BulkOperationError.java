package com.github.rwitzel.couchrepository.api.exceptions;

/**
 * Part of {@link BulkOperationException}.
 * 
 * @author rwitzel
 */
public class BulkOperationError {

    private String id;

    private String rev;

    private String error;

    private String reason;

    public BulkOperationError() {
        super();
    }

    public BulkOperationError(String id, String rev, String error, String reason) {
        super();
        this.id = id;
        this.rev = rev;
        this.error = error;
        this.reason = reason;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getRev() {
        return rev;
    }

    public void setRev(String rev) {
        this.rev = rev;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    @Override
    public String toString() {
        return "BulkOperationError [id=" + id + ", rev=" + rev + ", error=" + error + ", reason=" + reason + "]";
    }
}
