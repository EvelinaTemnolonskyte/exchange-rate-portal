package lt.fxportal.api.endpoints;

import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lt.fxportal.applicationservices.CurrencyApplicationService;
import lt.fxportal.applicationservices.models.CurrencyView;
import lt.fxportal.api.dtos.CurrencyDTO;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@AllArgsConstructor
@RestController
@RequestMapping(value = "/currencies")
public class CurrencyController {

    private final CurrencyApplicationService currencyApplicationService;

    @GetMapping
    public ResponseEntity<List<CurrencyDTO>> getCurrencies(
            @RequestParam(name = "withRates", defaultValue = "false") boolean withRates) {
        List<CurrencyView> currencies = withRates
                ? currencyApplicationService.getCurrenciesWithRates()
                : currencyApplicationService.getAllCurrencies();
        return ResponseEntity.ok(currencies.stream().map(this::toDto).toList());
    }

    @GetMapping("/{currencyCode}")
    public ResponseEntity<CurrencyDTO> getCurrencyByCode(
            @PathVariable @NotBlank @Pattern(regexp = "(?i)^[A-Z]{3}$") String currencyCode) {
        CurrencyView currency = currencyApplicationService.getCurrencyByCode(currencyCode);
        return ResponseEntity.ok(toDto(currency));
    }

    private CurrencyDTO toDto(CurrencyView currency) {
        return new CurrencyDTO(
                currency.code(),
                currency.englishName(),
                currency.numericCode(),
                currency.minorUnitDigits());
    }
}