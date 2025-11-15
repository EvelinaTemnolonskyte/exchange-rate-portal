package lt.fxportal.Mappers;

import org.springframework.stereotype.Component;

import lt.fxportal.API.DTOs.CurrencyDTO;
import lt.fxportal.Domain.Entities.Currency;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lt.fxportal.Domain.Entities.CurrencyData;
import lt.fxportal.Domain.Entities.CurrencyName;

@Component
public class CurrencyMapper {

    // Convert API data to DTO
    public CurrencyDTO toCurrencyDTO(CurrencyData data) {
        Optional<CurrencyName> nameEn = data.getNames().stream()
                .filter(n -> "en".equalsIgnoreCase(n.getLang()))
                .findFirst();

        return CurrencyDTO.builder()
                .code(data.getCode())
                .nameEn(nameEn.map(CurrencyName::getValue).orElse(data.getCode()))
                .numCode(data.getNumCode())
                .decimals(data.getDecimals())
                .build();
    }

    // Convert DTO to entity
    public Currency toCurrency(CurrencyDTO dto) {
        return Currency.builder()
            .code(dto.getCode())
            .nameEn(dto.getNameEn())
            .numCode(dto.getNumCode())
            .decimals(dto.getDecimals())
            .build();
    }

    // Convert entity to DTO
    public CurrencyDTO toCurrencyDTO(@NotNull @Valid Currency entity) {
        return CurrencyDTO.builder()
                .code(entity.getCode())
                .nameEn(entity.getNameEn())
                .numCode(entity.getNumCode())    
                .decimals(entity.getDecimals())      
                .build();
    }

    // Convert list of API data to DTOs
    public List<CurrencyDTO> toCurrencyDTOList(List<CurrencyData> dataList) {
        return dataList.stream()
                .map(this::toCurrencyDTO)
                .collect(Collectors.toList());
    }

    // Convert list of DTOs to entities
    public List<Currency> toCurrencyList(List<CurrencyDTO> dtoList) {
        return dtoList.stream()
                .map(this::toCurrency)
                .collect(Collectors.toList());
    }

    // Convert list of entities to DTOs
    public List<CurrencyDTO> toCurrencyDTOListFromEntities(List<Currency> entities) {
        return entities.stream()
                .map(this::toCurrencyDTO)
                .collect(Collectors.toList());
    }
}

