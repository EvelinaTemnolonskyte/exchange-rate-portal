package lt.fxportal.API.Endpoints;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.ExchangeRateDTO;
import lt.fxportal.API.DTOs.lbLtFxRates.DailyFxRate;
import lt.fxportal.Domain.Services.LbClient;

@AllArgsConstructor
@RestController
@RequestMapping("/rates")
public class ExchangeRateController {
    
    private final LbClient lbClient;

    @GetMapping(produces = "application/json")
    public List<DailyFxRate> getRates() throws Exception {
        return lbClient.getCurrentFxRatesXml();
    }
}

