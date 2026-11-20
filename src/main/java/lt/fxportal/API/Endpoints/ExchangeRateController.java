package lt.fxportal.api.endpoints;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lt.fxportal.applicationservices.ExchangeRateApplicationService;
import lt.fxportal.api.dtos.CurrencyConversionDTO;
import lt.fxportal.api.dtos.CurrencyConversionRequest;
import lt.fxportal.api.dtos.ExchangeRateDTO;

@AllArgsConstructor
@RestController
public class ExchangeRateController {
    
    private final ExchangeRateApplicationService exchangeRateApplicationService;

    @GetMapping("/rates")
    public ResponseEntity<List<ExchangeRateDTO>> getAllRates() {
        List<ExchangeRateDTO> exchangeRateDTOList = exchangeRateApplicationService.getAllRates();
        return ResponseEntity.ok(exchangeRateDTOList);
    }

    @GetMapping("/currencies/{currencyCode}/rates")
    public ResponseEntity<List<ExchangeRateDTO>> getRatesForCurrency(
            @PathVariable @NotBlank @Pattern(regexp = "(?i)^[A-Z]{3}$") String currencyCode,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fromDate,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate toDate) {

        List<ExchangeRateDTO> rates = exchangeRateApplicationService.getRatesForCurrency(currencyCode, fromDate, toDate);
        return ResponseEntity.ok(rates);
    }

    @GetMapping("/rates/latest")
    public ResponseEntity<List<ExchangeRateDTO>> getLatestRates() {
        List<ExchangeRateDTO> exchangeRateDTOList = exchangeRateApplicationService.getLatestRatesForAllCurrencies();
        return ResponseEntity.ok(exchangeRateDTOList);
    }

    @GetMapping("/conversions")
    public ResponseEntity<CurrencyConversionDTO> convert(
            @Valid @ModelAttribute CurrencyConversionRequest request) {
        CurrencyConversionDTO dto = exchangeRateApplicationService.convertAmount(
                request.fromCurrency(), request.toCurrency(), request.amount());
        return ResponseEntity.ok(dto);
    }
}