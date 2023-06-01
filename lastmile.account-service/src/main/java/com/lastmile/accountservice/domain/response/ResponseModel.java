package com.lastmile.accountservice.domain.response;

public class ResponseModel {

    private String message;
    private int internalCode;
    private Object body;

    public ResponseModel() {
        super();
    }

    public ResponseModel(String message, int internalCode, Object body) {
        super();
        this.message = message;
        this.internalCode = internalCode;
        this.body = body;
    }

    public ResponseModel(String message, int internalCode) {
        super();
        this.message = message;
        this.internalCode = internalCode;
    }

    public ResponseModel(int internalCode, Object body) {
        super();
        this.internalCode = internalCode;
        this.body = body;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getInternalCode() {
        return internalCode;
    }

    public void setInternalCode(int internalCode) {
        this.internalCode = internalCode;
    }

    public Object getBody() {
        return body;
    }

    public void setBody(Object body) {
        this.body = body;
    }

}
