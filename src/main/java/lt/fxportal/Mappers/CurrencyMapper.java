package lt.fxportal.Mappers;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.CurrencyDTO;
import lt.fxportal.Domain.Entities.Currency;

@Component
@AllArgsConstructor
public class CurrencyMapper {
    public CurrencyDTO toDTO(Currency currency) {
        return CurrencyDTO.builder()
                .code(currency.getCode())
                .nameEn(currency.getNameEn())
                .numCode(currency.getNumCode())
                .decimals(currency.getDecimals())
                .build();
    }

    public List<CurrencyDTO> toDTOList(List<Currency> currencyList){
        return currencyList.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
}
