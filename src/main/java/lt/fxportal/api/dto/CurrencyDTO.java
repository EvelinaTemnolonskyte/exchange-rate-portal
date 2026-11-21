package lt.fxportal.api.dto;

public record CurrencyDTO(
        String code,
        String englishName,
        String numericCode,
        Integer minorUnitDigits) {
}