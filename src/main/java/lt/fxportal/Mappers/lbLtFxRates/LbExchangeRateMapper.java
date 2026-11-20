package lt.fxportal.Mappers.lbLtFxRates;

import java.time.LocalDate;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyAmount;
import lt.fxportal.Domain.Entities.Currency;
import lt.fxportal.Domain.Entities.ExchangeRate;

@Mapper(componentModel = "spring")
public interface LbExchangeRateMapper {
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currency", source = "currency")
    @Mapping(target = "date", source = "date")
    @Mapping(target = "rate", source = "currencyAmount.amount")
    ExchangeRate toEntity(CurrencyAmount currencyAmount, Currency currency, LocalDate date);
}