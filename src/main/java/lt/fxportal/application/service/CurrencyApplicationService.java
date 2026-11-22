package lt.fxportal.application.service;

import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lt.fxportal.api.dto.CurrencyDTO;
import lt.fxportal.application.exception.CurrencyNotFoundException;
import lt.fxportal.api.mapper.CurrencyMapper;
import lt.fxportal.application.port.ReferenceDataImporter;
import lt.fxportal.domain.CurrencyCodes;
import lt.fxportal.infrastructure.repository.ICurrencyRepository;
import lt.fxportal.infrastructure.repository.IExchangeRateRepository;

@Service
@RequiredArgsConstructor
public class CurrencyApplicationService {

    private final ICurrencyRepository currencyRepository;
    private final IExchangeRateRepository exchangeRateRepository;
    private final CurrencyMapper currencyMapper;
    private final ReferenceDataImporter referenceDataImporter;

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
        Set<String> codes = exchangeRateRepository
                .findLatestRatesForAllCurrencies()
                .stream()
                .map(rate -> rate.getCurrency().getCode())
                .collect(Collectors.toCollection(HashSet::new));

        codes.add(CurrencyCodes.BASE_CURRENCY);

        return currencyMapper.toDTOList(currencyRepository.findAllById(codes));
    }
}