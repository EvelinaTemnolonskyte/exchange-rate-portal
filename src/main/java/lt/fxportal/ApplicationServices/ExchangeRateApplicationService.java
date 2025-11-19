package lt.fxportal.ApplicationServices;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.CurrencyConversionDTO;
import lt.fxportal.API.DTOs.ExchangeRateDTO;
import lt.fxportal.API.DTOs.lbLtFxRates.DailyFxRate;
import lt.fxportal.ApplicationServices.Exceptions.CurrencyNotFoundException;
import lt.fxportal.ApplicationServices.Exceptions.InvalidDateRangeException;
import lt.fxportal.Domain.Entities.ExchangeRate;
import lt.fxportal.Domain.Services.CurrencyConverter;
import lt.fxportal.InfrastructureServices.LbClient;
import lt.fxportal.Mappers.CurrencyConversionMapper;

import lt.fxportal.Mappers.ExchangeRateMapper;
import lt.fxportal.Mappers.lbLtFxRates.LbExchangeRateMapper;
import lt.fxportal.Repositories.ICurrencyRepository;
import lt.fxportal.Repositories.IExchangeRateRepository;


@Service
@AllArgsConstructor
public class ExchangeRateApplicationService {

    private final IExchangeRateRepository exchangeRateRepository;
    private final ICurrencyRepository currencyRepository;

    private final LbExchangeRateMapper lbExchangeRateMapper;
    private final ExchangeRateMapper exchangeRateMapper;
    private final CurrencyConversionMapper currencyConversionMapper;

    private final CurrencyConverter currencyConverter;
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

    public List<ExchangeRateDTO> getAllRates() {
        List<ExchangeRate> rates = exchangeRateRepository.findAll();
        return exchangeRateMapper.toDTOList(rates);
    }

    public List<ExchangeRateDTO> getLatestRatesForAllCurrencies() {
        List<ExchangeRate> latestRates = exchangeRateRepository.findLatestRatesForAllCurrencies();
        return exchangeRateMapper.toDTOList(latestRates);
    }

   public List<ExchangeRateDTO> getRatesForCurrency(String currency, LocalDate from, LocalDate to) {
        boolean currencyExists = currencyRepository.existsById(currency);
        if (!currencyExists) {
            throw new CurrencyNotFoundException("Currency not found: " + currency);
        }
        if (from != null && to != null && to.isBefore(from)) {
            throw new InvalidDateRangeException("'to' date cannot be before 'from' date");
        }
        List<ExchangeRate> rates = exchangeRateRepository.findByCurrencyAndOptionalDates(currency, from, to);
        return exchangeRateMapper.toDTOList(rates);
    }



    public CurrencyConversionDTO convertAmount(String from, String to, BigDecimal amount) {

        BigDecimal fromRate = from.equalsIgnoreCase("EUR") 
                ? BigDecimal.ONE 
                : exchangeRateRepository.findTopByCurrency_CodeOrderByDateDesc(from)
                    .map(ExchangeRate::getRate)
                    .orElseThrow(() -> 
                        new CurrencyNotFoundException("Currency not found: " + from));

        BigDecimal toRate = to.equalsIgnoreCase("EUR") 
                ? BigDecimal.ONE 
                : exchangeRateRepository.findTopByCurrency_CodeOrderByDateDesc(to)
                    .map(ExchangeRate::getRate)
                    .orElseThrow(() -> 
                        new CurrencyNotFoundException("Currency not found: " + to));

        BigDecimal rateUsed = currencyConverter.getRate(fromRate, toRate);
        BigDecimal convertedAmount = currencyConverter.convert(amount, rateUsed);

        return currencyConversionMapper.toDTO(from, to, amount, convertedAmount, rateUsed);
    }



}
