package com.assessment.tasks.exception;

import java.util.Map;

public class ErrorResponse {

    private String error;
    private String message;
    private Map<String, String> fields;

    public ErrorResponse(String error, String message) {
        this.error = error;
        this.message = message;
    }

    public ErrorResponse(String error, String message, Map<String, String> fields) {
        this.error = error;
        this.message = message;
        this.fields = fields;
    }

    public String getError() { return error; }
    public String getMessage() { return message; }
    public Map<String, String> getFields() { return fields; }
}