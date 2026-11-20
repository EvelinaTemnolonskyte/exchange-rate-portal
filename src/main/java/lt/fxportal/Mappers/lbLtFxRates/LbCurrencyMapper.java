package lt.fxportal.Mappers.lbLtFxRates;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyData;
import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyName;
import lt.fxportal.Domain.Entities.Currency;

@Mapper(componentModel = "spring")
public interface LbCurrencyMapper {

    @Mapping(target = "nameEn", expression = "java(resolveEnglishName(data))")
    Currency toEntity(CurrencyData data);

    default String resolveEnglishName(CurrencyData data) {
        return data.getNames() == null ? data.getCode() : data.getNames().stream()
                .filter(n -> "en".equalsIgnoreCase(n.getLang()))
                .map(CurrencyName::getValue)
                .findFirst()
                .orElse(data.getCode());
    }
}
