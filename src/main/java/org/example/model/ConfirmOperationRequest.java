package org.example.model;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmOperationRequest {
    @NotNull
    private String operationId;
    @NotNull
    private String code;

}
