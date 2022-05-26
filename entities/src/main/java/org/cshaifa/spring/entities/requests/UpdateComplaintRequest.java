package org.cshaifa.spring.entities.requests;

import org.cshaifa.spring.entities.Complaint;

public class UpdateComplaintRequest extends Request {
    Complaint complaint;

    public UpdateComplaintRequest(Complaint complaint) {
        this.complaint = complaint;
    }

    public Complaint getUpdatedComplaint() {
        return complaint;
    }

    public void setUpdatedComplaint(Complaint complaint) {
        this.complaint = complaint;
    }
}
