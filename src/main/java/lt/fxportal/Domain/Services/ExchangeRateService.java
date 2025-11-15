package lt.fxportal.Domain.Services;

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

    /**
     * Save list of exchange rates directly from XML entities
     */
    @Transactional
    public void saveAllRates(List<DailyFxRate> dailyFxRates) {
        List<ExchangeRate> entities = mapper.toEntityList(dailyFxRates);

        List<ExchangeRate> newRates = entities.stream()
                .filter(rate -> !exchangeRateRepository.existsByCurrencyCodeAndDate(rate.getCurrencyCode(), rate.getDate()))
                .toList();

        exchangeRateRepository.saveAll(newRates);
    }

    /**
     * Fetch all rates from DB
     */
    public List<ExchangeRate> getAllRates() {
        return exchangeRateRepository.findAll();
    }
}
