package lt.fxportal.Mappers.lbLtFxRates;

import org.springframework.stereotype.Component;

import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyData;
import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyName;
import lt.fxportal.Domain.Entities.Currency;


@Component
public class LbCurrencyMapper {

    public Currency toEntity(CurrencyData data) {
        String nameEn = data.getNames().stream()
                .filter(n -> "en".equalsIgnoreCase(n.getLang()))
                .map(CurrencyName::getValue)
                .findFirst()
                .orElse(data.getCode());

        return Currency.builder()
                .code(data.getCode())
                .nameEn(nameEn)
                .numCode(data.getNumCode())
                .decimals(data.getDecimals())
                .build();
    }
}
