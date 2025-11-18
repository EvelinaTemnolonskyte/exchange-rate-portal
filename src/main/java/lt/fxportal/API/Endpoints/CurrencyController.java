package lt.fxportal.API.Endpoints;

import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.CurrencyDTO;
import lt.fxportal.API.DTOs.ExchangeRateDTO;
import lt.fxportal.ApplicationServices.CurrencyApplicationService;

import java.time.LocalDate;
import java.util.List;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;



@AllArgsConstructor
@RestController
@RequestMapping("/currencies")
public class CurrencyController {

    private final CurrencyApplicationService currencyApplicationService;

    @GetMapping(produces = "application/xml")
    public ResponseEntity <List<CurrencyDTO>> getAllRates(){
        List<CurrencyDTO> exchangeRateDTOList = currencyApplicationService.getAllCurrencies();
        return ResponseEntity.ok().body(exchangeRateDTOList);
    }

    @GetMapping(value = "/by-code", produces = "application/xml")
    public ResponseEntity<CurrencyDTO> getCurrencyByCode(@RequestParam String currencyCode) {
        CurrencyDTO currencyDTO = currencyApplicationService.getCurrencyByCode(currencyCode);
        if(currencyDTO != null){
            return ResponseEntity.ok().body(currencyDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
}
