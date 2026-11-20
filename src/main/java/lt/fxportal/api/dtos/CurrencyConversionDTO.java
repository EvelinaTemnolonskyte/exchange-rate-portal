package lt.fxportal.api.dtos;

import java.math.BigDecimal;

public record CurrencyConversionDTO(
        String fromCurrency,
        String toCurrency,
        BigDecimal originalAmount,
        BigDecimal convertedAmount,
        BigDecimal rateUsed) {
}
