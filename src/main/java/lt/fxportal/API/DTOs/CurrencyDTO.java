package lt.fxportal.api.dtos;

public record CurrencyDTO(
        String code,
        String englishName,
        String numericCode,
        Integer minorUnitDigits) {
}