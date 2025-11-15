package lt.fxportal.Domain.Services;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;

@Component
@AllArgsConstructor
public class StartupRunner {

    private final CurrencyService currencyService;

    @EventListener(ApplicationReadyEvent.class)
    public void runAfterStartup() {
        try {
            currencyService.fetchAndSaveCurrencies();
            System.out.println("Currencies loaded on startup.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

