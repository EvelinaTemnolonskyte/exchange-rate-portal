package lt.fxportal.Mappers;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lt.fxportal.API.DTOs.ExchangeRateDTO;
import lt.fxportal.Domain.Entities.ExchangeRate;

@Component
public class ExchangeRateMapper {

    public ExchangeRate toExchangeRate(ExchangeRateDTO exchangeRateDTO){
        return ExchangeRate.builder()
            .currencyCode(exchangeRateDTO.getCurrencyCode())
            .rate(exchangeRateDTO.getRate())
            .date(LocalDate.parse(exchangeRateDTO.getDate()))
            .build();
    }
    
    public ExchangeRateDTO toExchangeRateDTO(@NotNull @Valid ExchangeRate exchangeRate){
        return ExchangeRateDTO.builder()
                .currencyCode(exchangeRate.getCurrencyCode())
                .rate(exchangeRate.getRate())
                .date(exchangeRate.getDate().toString())
                .build();
    }

    public List<ExchangeRate> toExchangeRateList(List<ExchangeRateDTO> dtoList) {
        return dtoList.stream()
                .map(this::toExchangeRate)
                .collect(Collectors.toList());
    }

    public List<ExchangeRateDTO> toExchangeRateDTOList(List<ExchangeRate> entityList) {
        return entityList.stream()
                .map(this::toExchangeRateDTO)
                .collect(Collectors.toList());
    }
}
