package org.example.service;

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
import java.util.UUID;


@Service
public class MoneyService {
    private static final BigDecimal COMMISSION_PERCENTAGE = BigDecimal.valueOf(0.01);
    public String processTransfer(TransferRequest transferRequest) throws TransferException {
        String operationId = UUID.randomUUID().toString();
        String status;
        if (transferRequest.getAmount().getValue() > 0) {
            TransferRequest.Amount amount = transferRequest.getAmount();
            BigDecimal totalAmount = BigDecimal.valueOf(amount.getValue());
            BigDecimal commission = totalAmount.multiply(COMMISSION_PERCENTAGE);
            status = "SUCCESS";
            logTransaction(transferRequest, operationId, status, commission.divide(BigDecimal.valueOf(100)));
        } else {
            status = "FAILURE";
            logTransaction(transferRequest, operationId, status, BigDecimal.ZERO);
            throw new TransferException("Invalid transfer request.");
        }
        return operationId;
    }

    public  String processConfirmation(ConfirmOperationRequest confirmOperationRequest) throws ConfirmOperationException {
        return confirmOperationRequest.getOperationId();
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
}