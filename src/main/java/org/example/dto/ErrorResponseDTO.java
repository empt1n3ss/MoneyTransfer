package org.example.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter 
public class ErrorResponseDTO {
    private String message;
    private int id;

    public ErrorResponseDTO(String message, int id) {
        this.message = message;
        this.id = id;
    }
}
