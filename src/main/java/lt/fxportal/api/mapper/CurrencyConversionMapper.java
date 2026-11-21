package lt.fxportal.api.mapper;

import java.math.BigDecimal;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import lt.fxportal.api.dto.CurrencyConversionDTO;

@Mapper(componentModel = "spring")
public interface CurrencyConversionMapper {
    @Mapping(target = "fromCurrency", source = "fromCurrency")
    @Mapping(target = "toCurrency", source = "toCurrency")
    @Mapping(target = "originalAmount", source = "originalAmount")
    @Mapping(target = "convertedAmount", source = "convertedAmount")
    @Mapping(target = "rateUsed", source = "rateUsed")
    CurrencyConversionDTO toDTO(String fromCurrency, String toCurrency,
                                BigDecimal originalAmount, BigDecimal convertedAmount,
                                BigDecimal rateUsed);
}
