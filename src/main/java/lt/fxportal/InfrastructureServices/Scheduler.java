package lt.fxportal.infrastructureservices;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.AllArgsConstructor;

import lt.fxportal.applicationservices.CurrencyApplicationService;
import lt.fxportal.applicationservices.ExchangeRateApplicationService;

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
            logger.error("Error fetching data: {}", e.getMessage(), e);
        }
    }

}
