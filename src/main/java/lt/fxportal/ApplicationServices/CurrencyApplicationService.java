package lt.fxportal.ApplicationServices;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

import lt.fxportal.API.DTOs.CurrencyDTO;
import lt.fxportal.API.DTOs.ExchangeRateDTO;
import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyData;
import lt.fxportal.ApplicationServices.Exceptions.CurrencyNotFoundException;
import lt.fxportal.Domain.Entities.Currency;
import lt.fxportal.InfrastructureServices.LbClient;
import lt.fxportal.Mappers.CurrencyMapper;
import lt.fxportal.Mappers.lbLtFxRates.LbCurrencyMapper;
import lt.fxportal.Repositories.ICurrencyRepository;
import lt.fxportal.Repositories.IExchangeRateRepository;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CurrencyApplicationService {

    private static final String BASE_CURRENCY = "EUR";

    private final ICurrencyRepository currencyRepository;
    private final LbClient lbClient;
    private final LbCurrencyMapper lbCurrencyMapper;
    private final CurrencyMapper currencyMapper;
    private final IExchangeRateRepository exchangeRateRepository;

    @Transactional
    public void fetchAndSaveCurrencies() throws Exception {
        List<CurrencyData> currencyDataList = lbClient.getCurrencyListXml();

        List<Currency> newCurrencies = currencyDataList.stream()
                .map(lbCurrencyMapper::toCurrencyEntity)
                .filter(c -> !currencyRepository.existsById(c.getCode()))
                .toList();

        if (!newCurrencies.isEmpty()) {
            currencyRepository.saveAll(newCurrencies);
        }
    }

    @Transactional(readOnly = true)
    public List<CurrencyDTO> getAllCurrencies() {
        return currencyMapper.toDTOList(currencyRepository.findAll());
    }

    @Transactional(readOnly = true)
    public CurrencyDTO getCurrencyByCode(String code) {
        return currencyRepository.findById(code)
                .map(currencyMapper::toDTO)
                .orElseThrow(() -> 
                    new CurrencyNotFoundException("Currency with code '" + code + "' not found")
                );
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
