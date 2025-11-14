package lt.fxportal.Domain.Services;

import java.util.List;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.ExchangeRateDTO;

@Service
@AllArgsConstructor
public class DailyFxRateScheduler {

    private final LbClient lbClient;
    private final ExchangeRateService exchangeRateService;

    //@Scheduled(cron = "0 0 9 * * *")
    @Scheduled(cron = "*/60 * * * * *")
    public void fetchAndSaveDailyRates() throws Exception {
        List<ExchangeRateDTO> dailyRates = lbClient.getCurrentRates();
        exchangeRateService.saveAllRates(dailyRates);
    }
}
