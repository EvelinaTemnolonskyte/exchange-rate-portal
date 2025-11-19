package lt.fxportal.Mappers;

import java.math.BigDecimal;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.CurrencyConversionDTO;

@Component
@AllArgsConstructor
public class CurrencyConversionMapper {
    
    public CurrencyConversionDTO toDTO(String fromCurrency, String toCurrency, 
                                      BigDecimal originalAmount, BigDecimal convertedAmount, 
                                      BigDecimal rateUsed){
        return CurrencyConversionDTO.builder()
                .fromCurrency(fromCurrency)
                .toCurrency(toCurrency)
                .originalAmount(originalAmount)
                .convertedAmount(convertedAmount)
                .rateUsed(rateUsed)
                .build();
    }

}
