package org.example.storemanager.dto.request.catalog;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;

@Data
public class SerialNumberBatchRequest {

    private List<String> serialNumbers;

    private List<SerialNumberItemRequest> serialDetails;

    private String status = "AVAILABLE"; // AVAILABLE, SOLD, WARRANTY, RETURNED...
}
