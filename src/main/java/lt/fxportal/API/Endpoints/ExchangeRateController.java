package lt.fxportal.API.Endpoints;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.ExchangeRateDTO;
import lt.fxportal.ApplicationServices.ExchangeRateApplicationService;
import lt.fxportal.Mappers.ExchangeRateMapper;

@AllArgsConstructor
@RestController
@RequestMapping("/rates")
public class ExchangeRateController {
    
    private final ExchangeRateApplicationService exchangeRateApplicationService;
    private final ExchangeRateMapper exchangeRateMapper;

    @GetMapping(produces = "application/xml")
    public List<ExchangeRateDTO> getRates() throws Exception {
        return exchangeRateMapper.toDTOList(exchangeRateApplicationService.getAllRates());
    }
}

