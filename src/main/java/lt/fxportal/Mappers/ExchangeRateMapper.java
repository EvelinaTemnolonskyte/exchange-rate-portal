package lt.fxportal.Mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import lt.fxportal.API.DTOs.ExchangeRateDTO;
import lt.fxportal.Domain.Entities.ExchangeRate;

@Mapper(componentModel = "spring")
public interface ExchangeRateMapper {
    @Mapping(target = "currency", source = "currency.code")
    ExchangeRateDTO toDTO(ExchangeRate exchangeRate);

    List<ExchangeRateDTO> toDTOList(List<ExchangeRate> exchangeRates);
}