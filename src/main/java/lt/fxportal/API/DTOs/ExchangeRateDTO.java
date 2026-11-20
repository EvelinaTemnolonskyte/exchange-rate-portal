package lt.fxportal.api.dtos;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ExchangeRateDTO(
        String currencyCode,
        LocalDate date,
        BigDecimal rate) {
}
