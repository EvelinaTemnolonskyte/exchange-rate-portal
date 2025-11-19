package lt.fxportal.API.Endpoints;

import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.CurrencyDTO;
import lt.fxportal.ApplicationServices.CurrencyApplicationService;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@AllArgsConstructor
@RestController
@RequestMapping(value = "/currencies", produces = "application/xml" )
public class CurrencyController {

    private final CurrencyApplicationService currencyApplicationService;

    @GetMapping
    public ResponseEntity <List<CurrencyDTO>> getAllCurrencies(){
        List<CurrencyDTO> exchangeRateDTOList = currencyApplicationService.getAllCurrencies();
        return ResponseEntity.ok().body(exchangeRateDTOList);
    }


    @GetMapping("/by-code")
    public ResponseEntity<CurrencyDTO> getCurrencyByCode(@RequestParam String currencyCode) {
        CurrencyDTO dto = currencyApplicationService.getCurrencyByCode(currencyCode);
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/with-rates")
    public ResponseEntity<List<CurrencyDTO>> getVisibleCurrencies() {
        List<CurrencyDTO> visibleCurrencies = currencyApplicationService.getCurrenciesWithRates();
        return ResponseEntity.ok(visibleCurrencies);
    }
    
}