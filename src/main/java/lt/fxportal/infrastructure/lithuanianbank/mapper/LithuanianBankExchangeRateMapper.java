package lt.fxportal.infrastructure.lithuanianbank.mapper;

import java.time.LocalDate;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import lt.fxportal.domain.entities.Currency;
import lt.fxportal.domain.entities.ExchangeRate;
import lt.fxportal.infrastructure.lithuanianbank.dto.CurrencyAmount;

@Mapper(componentModel = "spring")
public interface LithuanianBankExchangeRateMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "rate", source = "currencyAmount.amount")
    ExchangeRate toEntity(CurrencyAmount currencyAmount, Currency currency, LocalDate date);
}