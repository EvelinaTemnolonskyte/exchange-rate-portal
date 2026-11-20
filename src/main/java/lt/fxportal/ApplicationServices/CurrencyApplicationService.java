package lt.fxportal.applicationservices;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lt.fxportal.applicationservices.exceptions.CurrencyNotFoundException;
import lt.fxportal.domain.entities.Currency;
import lt.fxportal.infrastructureservices.LithuanianBankDataImportService;
import lt.fxportal.mappers.CurrencyMapper;
import lt.fxportal.repositories.ICurrencyRepository;
import lt.fxportal.repositories.IExchangeRateRepository;
import lt.fxportal.api.dtos.CurrencyDTO;

@Service
@AllArgsConstructor
public class CurrencyApplicationService {

    private static final String BASE_CURRENCY = "EUR";

    private final ICurrencyRepository currencyRepository;
    private final IExchangeRateRepository exchangeRateRepository;
    private final CurrencyMapper currencyMapper;
    private final LithuanianBankDataImportService lithuanianBankDataImportService;

    @Transactional
    public void fetchAndSaveCurrencies() throws Exception {
        lithuanianBankDataImportService.importCurrencies();
    }

    @Transactional(readOnly = true)
    public List<CurrencyDTO> getAllCurrencies() {
        return currencyMapper.toDTOList(currencyRepository.findAll());
    }

    @Transactional(readOnly = true)
    public CurrencyDTO getCurrencyByCode(String code) {
        return currencyRepository.findById(code.toUpperCase(Locale.ROOT))
            .map(currencyMapper::toDTO)
            .orElseThrow(() -> 
                new CurrencyNotFoundException("Currency with code '" + code + "' not found"));
    }

    @Transactional(readOnly = true)
    public List<CurrencyDTO> getCurrenciesWithRates() {
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

        return currencyMapper.toDTOList(currencies);
    }
}