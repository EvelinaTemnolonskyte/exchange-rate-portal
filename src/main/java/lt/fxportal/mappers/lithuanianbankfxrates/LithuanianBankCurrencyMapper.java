package lt.fxportal.mappers.lithuanianbankfxrates;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import lt.fxportal.domain.entities.Currency;
import lt.fxportal.api.dtos.lithuanianbankfxrates.CurrencyData;
import lt.fxportal.api.dtos.lithuanianbankfxrates.CurrencyName;

@Mapper(componentModel = "spring")
public interface LithuanianBankCurrencyMapper {

    @Mapping(target = "code", source = "currencyCode")
    @Mapping(target = "englishName", expression = "java(resolveEnglishName(data))")
    Currency toEntity(CurrencyData data);

    default String resolveEnglishName(CurrencyData data) {
        return data.getLocalizedNames() == null ? data.getCurrencyCode() : data.getLocalizedNames().stream()
                .filter(currencyName -> "en".equalsIgnoreCase(currencyName.getLanguageCode()))
                .map(CurrencyName::getLocalizedName)
                .findFirst()
                .orElse(data.getCurrencyCode());
    }
}