package org.example.controller;

import org.example.dto.ConfirmationResponseDTO;
import org.example.dto.TransferResponseDTO;
import org.example.model.ConfirmOperationException;
import org.example.model.ConfirmOperationRequest;
import org.example.model.TransferException;
import org.example.model.TransferRequest;
import org.example.service.MoneyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class MoneyControllerTest {

    private MoneyService service;
    private MoneyController controller;

    @BeforeEach
    void setUp() {
        service = Mockito.mock(MoneyService.class);
        controller = new MoneyController(service);
    }

    @Test
    void transfer_shouldReturnOperationId_whenTransferIsSuccessful() {
        TransferRequest.Amount amount = new TransferRequest.Amount();
        amount.setValue(1000);
        amount.setCurrency("RUR");

        TransferRequest request = new TransferRequest();
        request.setCardFromNumber("1234567812345678");
        request.setCardFromValidTill("12/25");
        request.setCardFromCVV("123");
        request.setCardToNumber("8765432187654321");
        request.setAmount(amount);

        TransferResponseDTO expectedResponse = new TransferResponseDTO("operationId123", "SUCCESS", null);
        when(service.processTransfer(any(TransferRequest.class))).thenReturn(expectedResponse);

        ResponseEntity<?> response = controller.transfer(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse.getOperationId(), ((TransferResponseDTO) response.getBody()).getOperationId());
    }


    @Test
    void transfer_shouldReturnError_whenTransferFails() {
        TransferRequest.Amount amount = new TransferRequest.Amount();
        amount.setValue(1000);
        amount.setCurrency("RUR");

        TransferRequest request = new TransferRequest();
        request.setCardFromNumber("1234567812345678");
        request.setCardFromValidTill("12/25");
        request.setCardFromCVV("123");
        request.setCardToNumber("8765432187654321");
        request.setAmount(amount);

        when(service.processTransfer(any(TransferRequest.class))).thenThrow(new TransferException("Transfer failed"));

        ResponseEntity<?> response = controller.transfer(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("Transfer failed", ((TransferResponseDTO) response.getBody()).getMessage());
    }

    @Test
    void confirmOperation_shouldReturnOperationId_whenConfirmationIsSuccessful() {
        ConfirmOperationRequest request = new ConfirmOperationRequest();
        request.setOperationId("operationId123");
        request.setCode("0000");

        ConfirmationResponseDTO expectedResponse = new ConfirmationResponseDTO("confirmationId123", "SUCCESS");
        when(service.processConfirmation(any(ConfirmOperationRequest.class))).thenReturn(expectedResponse);

        ResponseEntity<?> response = controller.confirmOperation(request);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertEquals(expectedResponse.getOperationId(), ((ConfirmationResponseDTO) response.getBody()).getOperationId());
    }

    @Test
    void confirmOperation_shouldReturnError_whenConfirmationFails() {
        ConfirmOperationRequest request = new ConfirmOperationRequest();
        request.setOperationId("operationId123");
        request.setCode("0000");

        when(service.processConfirmation(any(ConfirmOperationRequest.class)))
                .thenThrow(new ConfirmOperationException("FAILURE"));

        ResponseEntity<?> response = controller.confirmOperation(request);

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals("FAILURE", ((ConfirmationResponseDTO) response.getBody()).getStatus());
    }
}
