package lt.fxportal.application.models;

public record CurrencyView(
        String code,
        String englishName,
        String numericCode,
        Integer minorUnitDigits) {
}