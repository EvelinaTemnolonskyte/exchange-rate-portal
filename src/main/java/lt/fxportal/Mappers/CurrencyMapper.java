package lt.fxportal.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import lt.fxportal.domain.entities.Currency;
import lt.fxportal.api.dtos.CurrencyDTO;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    CurrencyDTO toDTO(Currency currency);

    List<CurrencyDTO> toDTOList(List<Currency> currencies);
}
