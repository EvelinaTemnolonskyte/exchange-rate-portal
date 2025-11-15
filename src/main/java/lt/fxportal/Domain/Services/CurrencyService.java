package lt.fxportal.Domain.Services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.CurrencyDTO;
import lt.fxportal.Domain.Entities.Currency;
import lt.fxportal.Domain.Entities.CurrencyData;
import lt.fxportal.Mappers.CurrencyMapper;
import lt.fxportal.Repositories.ICurrencyRepository;

@Service
@AllArgsConstructor
public class CurrencyService {

    private final ICurrencyRepository currencyRepository;
    private final CurrencyMapper currencyMapper;
    private final LbClient lbClient;

    @Transactional
    public void fetchAndSaveCurrencies() throws Exception {
        List<CurrencyData> currencyDataList = lbClient.getCurrencyListXml();

        List<CurrencyDTO> currencyDTOs = currencyMapper.toCurrencyDTOList(currencyDataList);

        List<Currency> currencyEntities = currencyMapper.toCurrencyList(currencyDTOs);

        List<Currency> newCurrencies = currencyEntities.stream()
            .filter(c -> !currencyRepository.existsById(c.getCode()))
            .toList();

        currencyRepository.saveAll(newCurrencies);  
    }

    public List<CurrencyDTO> getAllCurrencies() {
        List<Currency> currencies = currencyRepository.findAll();
        return currencyMapper.toCurrencyDTOListFromEntities(currencies);
    }
}





