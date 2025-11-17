package lt.fxportal.Mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.ExchangeRateDTO;
import lt.fxportal.Domain.Entities.ExchangeRate;

@Component
@AllArgsConstructor
public class ExchangeRateMapper {
    
    public ExchangeRateDTO toDTO(ExchangeRate exchangeRate){
        return ExchangeRateDTO.builder()
                .currency(exchangeRate.getCurrency().getCode())
                .date(exchangeRate.getDate())
                .rate(exchangeRate.getRate())
                .build();
    }

    public List<ExchangeRateDTO> toDTOList(List<ExchangeRate> exchangeRateList){
        return exchangeRateList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

}
