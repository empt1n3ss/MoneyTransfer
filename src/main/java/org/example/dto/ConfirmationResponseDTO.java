package org.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmationResponseDTO {
    private String operationId;
    private String status;

    public ConfirmationResponseDTO(String operationId, String status){
        this.operationId = operationId;
        this.status = status;
    }
}
