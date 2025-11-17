package lt.fxportal.API.DTOs;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRateDTO {
    private String currency;
    private LocalDate date;
    private double rate;
}
