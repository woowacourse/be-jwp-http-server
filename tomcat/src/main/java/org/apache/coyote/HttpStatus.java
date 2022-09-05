package org.apache.coyote;

public enum HttpStatus {
    OK(200, "OK"),
    BAD_REQUEST(400, "BAD REQUEST"),
    NOT_FOUND(404, "NOT FOUND"),
    INTERNAL_SERVER_ERROR(500, "INTERNAL SERVER ERROR");

    private final int statusNumber;
    private final String statusName;

    HttpStatus(final int statusNumber, final String statusName) {
        this.statusNumber = statusNumber;
        this.statusName = statusName;
    }

    public int getStatusNumber() {
        return statusNumber;
    }

    public String getStatusName() {
        return statusName;
    }
}
