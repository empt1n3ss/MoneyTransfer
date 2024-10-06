package org.example.controller;


import org.example.dto.ConfirmationResponseDTO;
import org.example.dto.ErrorResponseDTO;
import org.example.dto.TransferResponseDTO;
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
        String status;
        try {
            TransferResponseDTO response = service.processTransfer(transferRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (TransferException e) {
            status = "FAILURE";
            TransferResponseDTO errorResponse = new TransferResponseDTO(null, status, e.getMessage());
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/confirmOperation")
    public ResponseEntity<?> confirmOperation(@Valid @RequestBody ConfirmOperationRequest confirmOperationRequest) {
        String status;
        try {
            ConfirmationResponseDTO response = service.processConfirmation(confirmOperationRequest);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (ConfirmOperationException e) {
            status = "FAILURE";
            ConfirmationResponseDTO errorResponse = new ConfirmationResponseDTO(confirmOperationRequest.getOperationId(), status);
            return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
        }
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorResponseDTO errorResponse = new ErrorResponseDTO(errorMessage, 400);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
}

