package lt.fxportal.api.dtos;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;

public record CurrencyConversionRequest(
        @NotBlank
        @Pattern(regexp = "(?i)^[A-Z]{3}$", message = "must be a 3-letter currency code")
        String fromCurrency,
        @NotBlank
        @Pattern(regexp = "(?i)^[A-Z]{3}$", message = "must be a 3-letter currency code")
        String toCurrency,
        @NotNull
        @PositiveOrZero
        BigDecimal amount) {
}