package lt.fxportal.Mappers.lbLtFxRates;

import java.time.LocalDate;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyAmount;
import lt.fxportal.API.DTOs.lbLtFxRates.DailyFxRate;
import lt.fxportal.Domain.Entities.Currency;
import lt.fxportal.Domain.Entities.ExchangeRate;
import lt.fxportal.Repositories.ICurrencyRepository;

@Component
@AllArgsConstructor
public class ExchangeRateMapper {

    private final ICurrencyRepository currencyRepository;

    public ExchangeRate toEntity(CurrencyAmount currencyAmount, String date) {

        Currency currency = currencyRepository
                .findById(currencyAmount.getCurrencyCode())
                .orElseThrow(() -> new RuntimeException("Currency not found: " + currencyAmount.getCurrencyCode()));

        return ExchangeRate.builder()
                .currency(currency)
                .rate(currencyAmount.getAmount())
                .date(LocalDate.parse(date))
                .build();
    }

    public List<ExchangeRate> toEntityList(List<DailyFxRate> dailyFxRates) {
        return dailyFxRates.stream()
                .flatMap(daily -> daily.getCurrencyAmounts().stream()
                        .filter(ca -> !ca.getCurrencyCode().equals("EUR"))
                        .map(ca -> toEntity(ca, daily.getDate()))
                )
                .toList();
    }
}
