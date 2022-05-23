package org.cshaifa.spring.entities.responses;


import org.cshaifa.spring.entities.Complaint;

public class AddComplaintResponse extends Response {

    private Complaint complaint;
    private String message;

    public AddComplaintResponse(int requestId, boolean success, String message) {
        super(requestId, success);
        this.complaint = null;
        this.message = message;
    }

    public AddComplaintResponse(int requestId, boolean success, Complaint complaint, String message) {
        super(requestId, success);
        this.complaint = complaint;
        this.message = message;
    }

    public Complaint getComplaint() {
        return complaint;
    }
    public String getMessage() {
        return message;}
    }


