package lt.fxportal.Domain.Services;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.lbLtFxRates.DailyFxRate;
import lt.fxportal.Domain.Entities.ExchangeRate;
import lt.fxportal.Mappers.lbLtFxRates.ExchangeRateMapper;
import lt.fxportal.Repositories.IExchangeRateRepository;


@Service
@AllArgsConstructor
public class ExchangeRateService {

    private final IExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateMapper mapper;
    private final LbClient lbClient;

    @Transactional
    public void saveAllRates(List<DailyFxRate> dailyFxRates) {
        List<ExchangeRate> entities = mapper.toEntityList(dailyFxRates);

        List<ExchangeRate> newRates = entities.stream()
            .filter(rate -> !exchangeRateRepository
                    .existsByCurrency_CodeAndDate(rate.getCurrency().getCode(), rate.getDate()))
            .toList();
            
        exchangeRateRepository.saveAll(newRates);
    }

    @Transactional
    public void fetchAndSaveFxRatesHistory(int monthsBack, List<String> currencyCodes) throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusMonths(monthsBack);

        for (String code : currencyCodes) {
            List<DailyFxRate> fxRates = lbClient.getFxRatesForCurrency(code, from, today);
            saveAllRates(fxRates); 
        }
    }

    public List<ExchangeRate> getAllRates() {
        return exchangeRateRepository.findAll();
    }
}
