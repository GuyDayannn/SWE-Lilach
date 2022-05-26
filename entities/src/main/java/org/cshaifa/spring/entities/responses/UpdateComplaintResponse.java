package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.Complaint;

public class UpdateComplaintResponse extends Response {
    private Complaint complaint = null;

    public UpdateComplaintResponse(int requestId, boolean success) {
        super(requestId, success);
    }

    public UpdateComplaintResponse(int requestId, Complaint complaint) {
        super(requestId, true);
        this.complaint = complaint;
    }

    public Complaint getUpdatedComplaint() {
        return complaint;
    }
}

