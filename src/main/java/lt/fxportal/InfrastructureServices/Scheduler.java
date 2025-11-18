package lt.fxportal.InfrastructureServices;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;

import lt.fxportal.ApplicationServices.CurrencyApplicationService;
import lt.fxportal.ApplicationServices.ExchangeRateApplicationService;

@Service
@AllArgsConstructor
public class Scheduler {

    private final ExchangeRateApplicationService exchangeRateService;
    private final CurrencyApplicationService currencyService;
    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

    @Scheduled(cron = "0 0 0 * * *") 
    public void fetchAndSaveDailyRates() {
        try {
            currencyService.fetchAndSaveCurrencies();
            logger.info("Currencies loaded.");

            exchangeRateService.fetchAndSaveCurrentRates();
            logger.info("Current FX rates loaded.");
        } catch (Exception e) {
            logger.error("Error fetching daily FX rates: {}", e.getMessage(), e);
        }
    }

}
