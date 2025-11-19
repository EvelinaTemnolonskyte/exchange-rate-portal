package lt.fxportal.InfrastructureServices;

import lt.fxportal.ApplicationServices.ExchangeRateApplicationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lt.fxportal.ApplicationServices.CurrencyApplicationService;

@Component
@AllArgsConstructor
public class StartupRunner {

    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

    private final CurrencyApplicationService currencyService;
    private final ExchangeRateApplicationService exchangeRateApplicationService;


    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        try {
            currencyService.fetchAndSaveCurrencies();
            logger.info("Currencies loaded on startup.");

            exchangeRateApplicationService.fetchAndSaveCurrentRates();
            logger.info("Current FX rates loaded on startup.");

        } catch (Exception e) {
            logger.error("Error during startup runner execution: {}", e.getMessage(), e);
        }
    }

}