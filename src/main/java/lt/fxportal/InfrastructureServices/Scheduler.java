package lt.fxportal.InfrastructureServices;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.lbLtFxRates.DailyFxRate;
import lt.fxportal.ApplicationServices.CurrencyApplicationService;
import lt.fxportal.ApplicationServices.ExchangeRateApplicationService;

@Service
@AllArgsConstructor
public class Scheduler {

    private final LbClient lbClient;
    private final ExchangeRateApplicationService exchangeRateService;
    private final CurrencyApplicationService currencyService;

    @Scheduled(cron = "*/60 * * * * *") 
    public void fetchAndSaveDailyRates() throws Exception {
        currencyService.fetchAndSaveCurrencies();
        List<DailyFxRate> dailyRates = lbClient.getCurrentFxRatesXml();
        exchangeRateService.saveAllRates(dailyRates);
    }
}
