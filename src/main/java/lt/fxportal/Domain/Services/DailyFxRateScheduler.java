package lt.fxportal.Domain.Services;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.ExchangeRateDTO;
import lt.fxportal.API.DTOs.lbLtFxRates.DailyFxRate;

@Service
@AllArgsConstructor
public class DailyFxRateScheduler {

    private final LbClient lbClient;
    private final ExchangeRateService exchangeRateService;

    @Scheduled(cron = "*/60 * * * * *") // every minute for testing
    public void fetchAndSaveDailyRates() throws Exception {
        List<DailyFxRate> dailyRates = lbClient.getCurrentFxRatesXml();
        exchangeRateService.saveAllRates(dailyRates);
    }
}
