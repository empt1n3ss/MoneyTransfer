package org.example.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TransferRequest {
    @NotNull(message = "Card number is required")
    @Size(min = 16, max = 16, message = "Card number must be exactly 16 digits")
    @Pattern(regexp = "\\d{16}", message = "Card number must be numeric")
    private String cardFromNumber;

    @NotNull(message = "Card expiry date is required")
    @Pattern(regexp = "(0[1-9]|1[0-2])/\\d{2}", message = "Expiry date must be in MM/YY format")
    private String cardFromValidTill;

    @NotNull(message = "Card CVV is required")
    @Size(min = 3, max = 3, message = "CVV must be exactly 3 digits")
    @Pattern(regexp = "\\d{3}", message = "CVV must be numeric")
    private String cardFromCVV;

    @NotNull(message = "Target card number is required")
    @Size(min = 16, max = 16, message = "Target card number must be exactly 16 digits")
    @Pattern(regexp = "\\d{16}", message = "Target card number must be numeric")
    private String cardToNumber;

    @NotNull(message = "Amount is required")
    private Amount amount;

    @Getter
    @Setter
    public static class Amount {
        @Min(value = 1, message = "Transfer amount must be greater than 0")
        private long value;

        @NotNull(message = "Currency is required")
        private String currency;
    }
}
