package lt.fxportal.Domain.Services;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.lbLtFxRates.DailyFxRate;

@Service
@AllArgsConstructor
public class Scheduler {

    private final LbClient lbClient;
    private final ExchangeRateService exchangeRateService;
    private final CurrencyService currencyService;

    @Scheduled(cron = "0 0 1 * * *")
    public void updateCurrenciesDaily() throws Exception {
        currencyService.fetchAndSaveCurrencies();
        System.out.println("Currencies updated from LB.");
    }
    
    @Scheduled(cron = "*/60 * * * * *") 
    public void fetchAndSaveDailyRates() throws Exception {
        List<DailyFxRate> dailyRates = lbClient.getCurrentFxRatesXml();
        exchangeRateService.saveAllRates(dailyRates);
    }
}
