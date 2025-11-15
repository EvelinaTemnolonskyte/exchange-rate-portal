package lt.fxportal.Domain.Services;

import java.util.List;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class StartupRunner {

    private final CurrencyService currencyService;
    private final ExchangeRateService exchangeRateService;

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        try {
            currencyService.fetchAndSaveCurrencies();
            System.out.println("Currencies loaded on startup.");
            List<String> currencyCodes = currencyService.getAllCurrencyCodes();
            exchangeRateService.fetchAndSaveFxRatesHistory(3, currencyCodes);
            System.out.println("Initial FX rates for past 3 months saved.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}