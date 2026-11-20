package lt.fxportal.Mappers;

import java.util.List;

import org.mapstruct.Mapper;
import lt.fxportal.API.DTOs.CurrencyDTO;
import lt.fxportal.Domain.Entities.Currency;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    CurrencyDTO toDTO(Currency currency);

    List<CurrencyDTO> toDTOList(List<Currency> currencies);
}
