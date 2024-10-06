package org.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferResponseDTO {
    private String operationId;
    private String status;
    private String message;

    public TransferResponseDTO(String operationId, String status, String message){
        this.operationId = operationId;
        this.status = status;
        this.message = message;
    }
}
