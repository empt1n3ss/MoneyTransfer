package org.example.service;

import org.example.dto.ConfirmationResponseDTO;
import org.example.dto.TransferResponseDTO;
import org.example.model.ConfirmOperationException;
import org.example.model.ConfirmOperationRequest;
import org.example.model.TransferException;
import org.example.model.TransferRequest;
import org.springframework.stereotype.Service;

import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@Service
public class MoneyService {
    private static final BigDecimal COMMISSION_PERCENTAGE = BigDecimal.valueOf(0.01);
    private static final String CONFIRMATION_CODE = "0000";
    private final Map<String, String> transferStorage = new HashMap<>();
    public TransferResponseDTO processTransfer(TransferRequest transferRequest) throws TransferException {
        String operationId = UUID.randomUUID().toString();
        String status;
        String message;
        if (transferRequest.getAmount().getValue() > 0) {
            transferStorage.put(operationId, CONFIRMATION_CODE);
            status = "PENDING";
            message = "Invalid transfer request: Amount must be greater than zero.";
            logTransaction(transferRequest, operationId, status, calculateCommission(transferRequest.getAmount().getValue()));
        } else {
            status = "FAILURE";
            message = "Invalid transfer request: Amount must be greater than zero.";
            throw new TransferException(message);
        }
        return new TransferResponseDTO(operationId, status, message);
    }

    public ConfirmationResponseDTO processConfirmation(ConfirmOperationRequest confirmOperationRequest) throws ConfirmOperationException {
        String operationId = confirmOperationRequest.getOperationId();
        String providedCode = confirmOperationRequest.getCode();
        String status;
        String message;
        if (transferStorage.containsKey(operationId) && CONFIRMATION_CODE.equals(providedCode)) {
            transferStorage.remove(operationId);
            status = "SUCCESS";
            logConfirmation(operationId, status);
        } else {
            status = "FAILURE";
            message = "Invalid operation ID or confirmation code.";
            throw new ConfirmOperationException(message);
        }
        return new ConfirmationResponseDTO(operationId, status);
    }

    private BigDecimal calculateCommission(long amount) {
        BigDecimal totalAmount = BigDecimal.valueOf(amount).divide(BigDecimal.valueOf(100));
        return totalAmount.multiply(COMMISSION_PERCENTAGE).setScale(2, BigDecimal.ROUND_HALF_UP);
    }

    private void logTransaction(TransferRequest request, String operationId, String status, BigDecimal commission) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        BigDecimal amountInRubles = BigDecimal.valueOf(request.getAmount().getValue()).divide(BigDecimal.valueOf(100));

        try (FileWriter writer = new FileWriter("transfer_log.txt", true)) {
            writer.write(LocalDateTime.now().format(formatter) + " | From: " + request.getCardFromNumber() +
                    " | To: " + request.getCardToNumber() +
                    " | Amount: " + amountInRubles + " " + request.getAmount().getCurrency() +
                    " | Commission: " + commission +
                    " | Operation ID: " + operationId +
                    " | Status: " + status + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void logConfirmation(String operationId, String status) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        try (FileWriter writer = new FileWriter("transfer_log.txt", true)) {
            writer.write(LocalDateTime.now().format(formatter) + " | Operation ID: " + operationId +
                    " | Status: " + status + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}