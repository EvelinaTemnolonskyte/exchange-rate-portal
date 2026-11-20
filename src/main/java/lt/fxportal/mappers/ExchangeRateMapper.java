package lt.fxportal.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import lt.fxportal.domain.entities.ExchangeRate;
import lt.fxportal.api.dtos.ExchangeRateDTO;

@Mapper(componentModel = "spring")
public interface ExchangeRateMapper {
    @Mapping(target = "currencyCode", source = "currency.code")
    ExchangeRateDTO toDTO(ExchangeRate exchangeRate);

    List<ExchangeRateDTO> toDTOList(List<ExchangeRate> exchangeRates);
}