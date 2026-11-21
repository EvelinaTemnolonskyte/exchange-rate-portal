package lt.fxportal.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import lt.fxportal.domain.entity.ExchangeRate;
import lt.fxportal.api.dto.ExchangeRateDTO;

@Mapper(componentModel = "spring")
public interface ExchangeRateMapper {
    @Mapping(target = "currencyCode", source = "currency.code")
    ExchangeRateDTO toDTO(ExchangeRate exchangeRate);

    List<ExchangeRateDTO> toDTOList(List<ExchangeRate> exchangeRates);
}