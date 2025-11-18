package lt.fxportal.ApplicationServices;

import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.ExchangeRateDTO;
import lt.fxportal.API.DTOs.lbLtFxRates.DailyFxRate;
import lt.fxportal.Domain.Entities.ExchangeRate;
import lt.fxportal.InfrastructureServices.LbClient;
import lt.fxportal.Mappers.ExchangeRateMapper;
import lt.fxportal.Mappers.lbLtFxRates.LbExchangeRateMapper;
import lt.fxportal.Repositories.IExchangeRateRepository;


@Service
@AllArgsConstructor
public class ExchangeRateApplicationService {

    private final IExchangeRateRepository exchangeRateRepository;
    private final LbExchangeRateMapper lbExchangeRateMapper;
    private final ExchangeRateMapper exchangeRateMapper;
    private final LbClient lbClient;

    @Transactional
    public void saveAllRates(List<DailyFxRate> dailyFxRates) {
        List<ExchangeRate> entities = lbExchangeRateMapper.toEntityList(dailyFxRates);

        List<ExchangeRate> newRates = entities.stream()
            .filter(rate -> !exchangeRateRepository
                    .existsByCurrency_CodeAndDate(rate.getCurrency().getCode(), rate.getDate()))
            .toList();

        exchangeRateRepository.saveAll(newRates);
    }

    @Transactional
    public void fetchAndSaveCurrentRates() throws Exception {
        List<DailyFxRate> dailyFxRates = lbClient.getCurrentFxRatesXml();
        saveAllRates(dailyFxRates);
    }

   /*  @Transactional
    public void fetchAndSaveFxRatesHistory(int daysBack, List<String> currencyCodes) throws Exception {
        LocalDate today = LocalDate.now();
        LocalDate from = today.minusDays(daysBack); 
        
        for (String code : currencyCodes) {
            List<DailyFxRate> fxRates = lbClient.getFxRatesForCurrency(code, from, today);
            saveAllRates(fxRates); 
        }
    } */


    public List<ExchangeRateDTO> getAllRates() {
        List<ExchangeRate> rates = exchangeRateRepository.findAll();
        return exchangeRateMapper.toDTOList(rates);
    }

    public List<ExchangeRateDTO> getLatestRatesForAllCurrencies() {
        List<ExchangeRate> latestRates = exchangeRateRepository.findLatestRatesForAllCurrencies();
        return exchangeRateMapper.toDTOList(latestRates);
    }

    public List<ExchangeRateDTO> getRatesForCurrency(String currency, LocalDate from, LocalDate to) {
        List<ExchangeRate> rates = exchangeRateRepository.findByCurrencyAndOptionalDates(currency, from, to);
        return exchangeRateMapper.toDTOList(rates);
    }

}
