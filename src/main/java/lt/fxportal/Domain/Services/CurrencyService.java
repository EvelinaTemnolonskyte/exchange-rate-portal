package lt.fxportal.Domain.Services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyData;
import lt.fxportal.Domain.Entities.Currency;
import lt.fxportal.Mappers.lbLtFxRates.CurrencyMapper;
import lt.fxportal.Repositories.ICurrencyRepository;

import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class CurrencyService {

    private final ICurrencyRepository currencyRepository;
    private final LbClient lbClient;
    private final CurrencyMapper currencyMapper;
    
    @Transactional
    public void fetchAndSaveCurrencies() throws Exception {
        List<CurrencyData> currencyDataList = lbClient.getCurrencyListXml();

       
        List<Currency> newCurrencies = currencyDataList.stream()
                .map(currencyMapper::toCurrencyEntity)
                .filter(c -> !currencyRepository.existsById(c.getCode()))
                .collect(Collectors.toList());
        if (!newCurrencies.isEmpty()) {
            currencyRepository.saveAll(newCurrencies);
        }
    }

    public List<Currency> getAllCurrencies() {
        return currencyRepository.findAll();
    }
}






