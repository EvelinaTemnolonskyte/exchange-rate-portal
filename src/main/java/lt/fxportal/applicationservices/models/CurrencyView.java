package lt.fxportal.applicationservices.models;

public record CurrencyView(
        String code,
        String englishName,
        String numericCode,
        Integer minorUnitDigits) {
}