package lt.fxportal.API.DTOs;

import java.math.BigDecimal;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyConversionDTO {
    @NotNull
    private String fromCurrency;

    @NotNull
    private String toCurrency;

    @NotNull
    private BigDecimal originalAmount;

    @NotNull
    private BigDecimal convertedAmount;

    @NotNull
    private BigDecimal rateUsed; 
}
