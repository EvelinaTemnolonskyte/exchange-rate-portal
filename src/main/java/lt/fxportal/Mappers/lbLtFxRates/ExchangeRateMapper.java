package lt.fxportal.Mappers.lbLtFxRates;

import java.time.LocalDate;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyAmount;
import lt.fxportal.API.DTOs.lbLtFxRates.DailyFxRate;
import lt.fxportal.Domain.Entities.ExchangeRate;
import lt.fxportal.Repositories.ICurrencyRepository;

@Component
@AllArgsConstructor
public class ExchangeRateMapper {

    private final ICurrencyRepository currencyRepository;
    private static final Logger log = LoggerFactory.getLogger(ExchangeRateMapper.class);

    public Optional<ExchangeRate> toEntity(CurrencyAmount currencyAmount, String date) {
        return currencyRepository.findById(currencyAmount.getCurrencyCode())
                .map(currency -> ExchangeRate.builder()
                        .currency(currency)
                        .rate(currencyAmount.getAmount())
                        .date(LocalDate.parse(date))
                        .build()
                ).or(() -> {
                    log.warn("Skipped unknown currency: {}", currencyAmount.getCurrencyCode());
                    return Optional.empty();
                });
    }

    public List<ExchangeRate> toEntityList(List<DailyFxRate> dailyFxRates) {
        return dailyFxRates.stream()
                .flatMap(daily -> daily.getCurrencyAmounts().stream()
                        .filter(ca -> !ca.getCurrencyCode().equals("EUR")) 
                        .map(ca -> toEntity(ca, daily.getDate()))
                        .flatMap(Optional::stream) 
                )
                .toList();
    }
}