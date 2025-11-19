package lt.fxportal.API.DTOs;

import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateDTO {

    @NotNull
    private String currency;

    @NotNull
    private LocalDate date;

    @NotNull
    private BigDecimal rate;
}
