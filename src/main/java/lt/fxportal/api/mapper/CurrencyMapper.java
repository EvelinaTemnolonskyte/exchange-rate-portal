package lt.fxportal.api.mapper;

import java.util.List;

import org.mapstruct.Mapper;

import lt.fxportal.api.dto.CurrencyDTO;
import lt.fxportal.domain.entity.Currency;

@Mapper(componentModel = "spring")
public interface CurrencyMapper {
    CurrencyDTO toDTO(Currency currency);

    List<CurrencyDTO> toDTOList(List<Currency> currencies);
}