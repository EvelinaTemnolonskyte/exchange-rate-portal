package lt.fxportal.api.controller;

import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lt.fxportal.application.service.CurrencyApplicationService;
import lt.fxportal.api.dto.CurrencyDTO;

import java.util.List;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/currencies")
public class CurrencyController {

    private final CurrencyApplicationService currencyApplicationService;

    @GetMapping
    public ResponseEntity<List<CurrencyDTO>> getCurrencies(
            @RequestParam(defaultValue = "false") boolean withRates) {
        List<CurrencyDTO> currencies = withRates
                ? currencyApplicationService.getCurrenciesWithRates()
                : currencyApplicationService.getAllCurrencies();
        return ResponseEntity.ok(currencies);
    }

    @GetMapping("/{currencyCode}")
    public ResponseEntity<CurrencyDTO> getCurrencyByCode(
            @PathVariable @NotBlank @Pattern(regexp = "(?i)^[A-Z]{3}$") String currencyCode) {
        CurrencyDTO currency = currencyApplicationService.getCurrencyByCode(currencyCode);
        return ResponseEntity.ok(currency);
    }
}