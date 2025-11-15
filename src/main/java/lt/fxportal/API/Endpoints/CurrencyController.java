package lt.fxportal.API.Endpoints;

import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyData;
import lt.fxportal.Domain.Services.LbClient;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;


@AllArgsConstructor
@RestController
@RequestMapping("/currencies")
public class CurrencyController {
    
    private final LbClient lbClient;

    @GetMapping(produces = "application/json")
    public List<CurrencyData> getCurrencies() throws Exception {
        return lbClient.getCurrencyListXml(); 
    }
    
}
