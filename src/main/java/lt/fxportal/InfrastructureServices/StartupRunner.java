package lt.fxportal.InfrastructureServices;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import lt.fxportal.ApplicationServices.CurrencyApplicationService;
import lt.fxportal.ApplicationServices.ExchangeRateApplicationService;

@Component
@AllArgsConstructor
public class StartupRunner {
    private static final Logger logger = LoggerFactory.getLogger(StartupRunner.class);

    private final CurrencyApplicationService currencyService;
    private final ExchangeRateApplicationService exchangeRateService;

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        try {
            currencyService.fetchAndSaveCurrencies();
            logger.info("Currencies loaded on startup.");

            /*List<String> currencyCodes = currencyService.getAllCurrencyCodes();
            exchangeRateService.fetchAndSaveFxRatesHistory(7, currencyCodes);
            logger.info("Initial FX rates for past 7 days saved.");*/
        } catch (Exception e) {
            logger.error("Error during startup runner execution: {}", e.getMessage(), e);
        }
    }

}