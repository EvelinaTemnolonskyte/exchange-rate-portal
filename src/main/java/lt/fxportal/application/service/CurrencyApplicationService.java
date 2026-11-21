package lt.fxportal.application.service;

import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lt.fxportal.api.dto.CurrencyDTO;
import lt.fxportal.application.exception.CurrencyNotFoundException;
import lt.fxportal.api.mapper.CurrencyMapper;
import lt.fxportal.application.port.ReferenceDataImporter;
import lt.fxportal.domain.CurrencyCodes;
import lt.fxportal.domain.entity.Currency;
import lt.fxportal.infrastructure.repository.ICurrencyRepository;
import lt.fxportal.infrastructure.repository.IExchangeRateRepository;

@Service
@AllArgsConstructor
public class CurrencyApplicationService {

    private final ICurrencyRepository currencyRepository;
    private final IExchangeRateRepository exchangeRateRepository;
    private final CurrencyMapper currencyMapper;
    private final ReferenceDataImporter referenceDataImporter;

    @Transactional
    public void importCurrencies() {
        referenceDataImporter.importCurrencies();
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

        currencyRepository.findById(CurrencyCodes.BASE_CURRENCY).ifPresent(base -> {
            if (currencies.stream().noneMatch(c -> c.getCode().equals(CurrencyCodes.BASE_CURRENCY))) {
                currencies.add(base);
            }
        });

        return currencyMapper.toDTOList(currencies);
    }
}