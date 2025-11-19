package lt.fxportal.API.Endpoints;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.CurrencyConversionDTO;
import lt.fxportal.API.DTOs.ExchangeRateDTO;
import lt.fxportal.ApplicationServices.ExchangeRateApplicationService;

@AllArgsConstructor
@RestController
@RequestMapping(value = "/rates", produces = "application/xml")
public class ExchangeRateController {
    
    private final ExchangeRateApplicationService exchangeRateApplicationService;

    @GetMapping
    public ResponseEntity <List<ExchangeRateDTO>> getAllRates(){
        List<ExchangeRateDTO> exchangeRateDTOList = exchangeRateApplicationService.getAllRates();
        return ResponseEntity.ok().body(exchangeRateDTOList);
    }

    @GetMapping("/by-currency")
    public ResponseEntity<List<ExchangeRateDTO>> getRatesByCurrency(
            @RequestParam String currency,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to){

        List<ExchangeRateDTO> rates = exchangeRateApplicationService.getRatesForCurrency(currency, from, to);
        return ResponseEntity.ok(rates);
    }

    @GetMapping("/latest")
    public ResponseEntity <List<ExchangeRateDTO>> getLastestRates(){
        List<ExchangeRateDTO> exchangeRateDTOList = exchangeRateApplicationService.getLatestRatesForAllCurrencies();
        return ResponseEntity.ok().body(exchangeRateDTOList);
    }

   @GetMapping("/convert")
    public ResponseEntity<CurrencyConversionDTO> getConversionAmount(
            @RequestParam String from,
            @RequestParam String to,
            @RequestParam BigDecimal amount) {

        CurrencyConversionDTO dto = exchangeRateApplicationService.convertAmount(from, to, amount);
        return ResponseEntity.ok(dto);
    }
    
    
}