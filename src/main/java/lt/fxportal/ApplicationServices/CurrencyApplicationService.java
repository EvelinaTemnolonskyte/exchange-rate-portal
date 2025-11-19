package lt.fxportal.ApplicationServices;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;

import lt.fxportal.API.DTOs.CurrencyDTO;
import lt.fxportal.API.DTOs.ExchangeRateDTO;
import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyData;
import lt.fxportal.Domain.Entities.Currency;
import lt.fxportal.InfrastructureServices.LbClient;
import lt.fxportal.Mappers.CurrencyMapper;
import lt.fxportal.Mappers.lbLtFxRates.LbCurrencyMapper;
import lt.fxportal.Repositories.ICurrencyRepository;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CurrencyApplicationService {

    private final ICurrencyRepository currencyRepository;
    private final LbClient lbClient;
    private final LbCurrencyMapper lbcurrencyMapper;
    private final CurrencyMapper currencyMapper;
    private final ExchangeRateApplicationService exchangeRateApplicationService;
    
    @Transactional
    public void fetchAndSaveCurrencies() throws Exception {
        List<CurrencyData> currencyDataList = lbClient.getCurrencyListXml();

       
        List<Currency> newCurrencies = currencyDataList.stream()
                .map(lbcurrencyMapper::toCurrencyEntity)
                .filter(c -> !currencyRepository.existsById(c.getCode()))
                .collect(Collectors.toList());
        if (!newCurrencies.isEmpty()) {
            currencyRepository.saveAll(newCurrencies);
        }
    }

    public List<CurrencyDTO> getAllCurrencies() {
        List<Currency> currencies = currencyRepository.findAll();
        return currencyMapper.toDTOList(currencies);
    }

    public CurrencyDTO getCurrencyByCode(String code) {
        return currencyRepository.findById(code)
                .map(currencyMapper::toDTO)
                .orElse(null);
    }
    
    public List<CurrencyDTO> getCurrenciesWithRates() {

        List<String> codesWithRates = exchangeRateApplicationService
                .getLatestRatesForAllCurrencies()
                .stream()
                .map(ExchangeRateDTO::getCurrency)
                .distinct()
                .toList();

        List<Currency> currencies = currencyRepository.findAllById(codesWithRates);

        currencyRepository.findById("EUR").ifPresent(euro -> {
            if (currencies.stream().noneMatch(c -> c.getCode().equals("EUR"))) {
                currencies.add(euro);
            }
        });

        return currencyMapper.toDTOList(currencies);
    }



}