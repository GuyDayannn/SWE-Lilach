package org.cshaifa.spring.entities.responses;

import org.cshaifa.spring.entities.CatalogItem;
import org.cshaifa.spring.entities.Complaint;

import java.util.List;

public class GetComplaintsResponse extends Response {
    List<Complaint> complaintList = null;

    public GetComplaintsResponse(int requestId, boolean success) {
        super(requestId, success);
    }

    public GetComplaintsResponse(int requestId, List<Complaint> complaintList) {
        super(requestId, true);
        this.complaintList = complaintList;
    }

    public List<Complaint> getComplaintList() {
        return complaintList;
    }

}
