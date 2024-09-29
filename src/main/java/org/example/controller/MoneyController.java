package org.example.controller;


import org.example.model.ConfirmOperationException;
import org.example.model.ConfirmOperationRequest;
import org.example.model.TransferException;
import org.example.model.TransferRequest;
import org.example.service.MoneyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.Valid;

@RestController
public class MoneyController {
    private final MoneyService service;

    @Autowired
    public MoneyController(MoneyService service) {
        this.service = service;
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransferRequest transferRequest) {
        try {
            String operationId = service.processTransfer(transferRequest);

            return ResponseEntity.ok(
                    Map.of(
                            "operationId", operationId
                    )
            );
        } catch (TransferException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "message", e.getMessage(),
                            "id", 500
                    )
            );
        }
    }

    @PostMapping("/confirmOperation")
    public ResponseEntity<?> confirmOperation(@Valid @RequestBody ConfirmOperationRequest confirmOperationRequest) {
        try {
            String operationId = service.processConfirmation(confirmOperationRequest);
            return ResponseEntity.ok(
                    Map.of(
                            "operationId", operationId
                    )
            );
        } catch (ConfirmOperationException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    Map.of(
                            "message", e.getMessage(),
                            "id", 500
                    )
            );
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                Map.of(
                        "message", errorMessage,
                        "id", 400
                )
        );
    }
}

