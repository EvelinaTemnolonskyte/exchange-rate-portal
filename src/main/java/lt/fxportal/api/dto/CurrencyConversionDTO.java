package lt.fxportal.api.dto;

import java.math.BigDecimal;

public record CurrencyConversionDTO(
        String fromCurrency,
        String toCurrency,
        BigDecimal originalAmount,
        BigDecimal convertedAmount,
        BigDecimal rateUsed) {
}
