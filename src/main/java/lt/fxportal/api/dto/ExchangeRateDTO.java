package lt.fxportal.api.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRateDTO(
        String currencyCode,
        LocalDate date,
        BigDecimal rate) {
}
