package lt.fxportal.Mappers.lbLtFxRates;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lt.fxportal.API.DTOs.ExchangeRateDTO;
import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyAmount;
import lt.fxportal.API.DTOs.lbLtFxRates.DailyFxRate;
import lt.fxportal.Domain.Entities.ExchangeRate;

@Component
public class ExchangeRateMapper {

    public ExchangeRate toEntity(CurrencyAmount currencyAmount, String date) {
        return ExchangeRate.builder()
                .currencyCode(currencyAmount.getCurrencyCode())
                .rate(currencyAmount.getAmount())
                .date(LocalDate.parse(date))
                .build();
    }

    public List<ExchangeRate> toEntityList(List<DailyFxRate> dailyFxRates) {
        return dailyFxRates.stream()
                .flatMap(daily -> daily.getCurrencyAmounts().stream()
                        .filter(ca -> !ca.getCurrencyCode().equals("EUR")) // skip EUR
                        .map(ca -> toEntity(ca, daily.getDate()))
                )
                .collect(Collectors.toList());
    }
}
