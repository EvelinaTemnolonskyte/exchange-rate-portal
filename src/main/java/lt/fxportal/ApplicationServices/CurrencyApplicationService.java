package lt.fxportal.applicationservices;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lt.fxportal.applicationservices.exceptions.CurrencyNotFoundException;
import lt.fxportal.applicationservices.mappers.CurrencyViewMapper;
import lt.fxportal.applicationservices.models.CurrencyView;
import lt.fxportal.applicationservices.ports.ReferenceDataImporter;
import lt.fxportal.domain.entities.Currency;
import lt.fxportal.repositories.ICurrencyRepository;
import lt.fxportal.repositories.IExchangeRateRepository;

@Service
@AllArgsConstructor
public class CurrencyApplicationService {

    private static final String BASE_CURRENCY = "EUR";

    private final ICurrencyRepository currencyRepository;
    private final IExchangeRateRepository exchangeRateRepository;
    private final CurrencyViewMapper currencyViewMapper;
    private final ReferenceDataImporter referenceDataImporter;

    @Transactional
    public void fetchAndSaveCurrencies() {
        referenceDataImporter.importCurrencies();
    }

    @Transactional(readOnly = true)
    public List<CurrencyView> getAllCurrencies() {
        return currencyViewMapper.toViewList(currencyRepository.findAll());
    }

    @Transactional(readOnly = true)
    public CurrencyView getCurrencyByCode(String code) {
        return currencyRepository.findById(code.toUpperCase(Locale.ROOT))
            .map(currencyViewMapper::toView)
            .orElseThrow(() -> 
                new CurrencyNotFoundException("Currency with code '" + code + "' not found"));
    }

    @Transactional(readOnly = true)
    public List<CurrencyView> getCurrenciesWithRates() {
        List<String> codesWithRates = exchangeRateRepository
                .findLatestRatesForAllCurrencies()
                .stream()
                .map(rate -> rate.getCurrency().getCode())
                .distinct()
                .toList();

        List<Currency> currencies = currencyRepository.findAllById(codesWithRates);

        currencyRepository.findById(BASE_CURRENCY).ifPresent(base -> {
            if (currencies.stream().noneMatch(c -> c.getCode().equals(BASE_CURRENCY))) {
                currencies.add(base);
            }
        });

        return currencyViewMapper.toViewList(currencies);
    }
}