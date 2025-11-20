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
import lt.fxportal.ApplicationServices.Exceptions.InvalidAmountException;
import lt.fxportal.ApplicationServices.Exceptions.InvalidDateRangeException;
import lt.fxportal.Domain.Entities.ExchangeRate;
import lt.fxportal.Domain.Services.CurrencyConverter;
import lt.fxportal.InfrastructureServices.LbClient;
import lt.fxportal.InfrastructureServices.LbDataImportService;
import lt.fxportal.Mappers.CurrencyConversionMapper;

import lt.fxportal.Mappers.ExchangeRateMapper;
import lt.fxportal.Mappers.lbLtFxRates.LbExchangeRateMapper;
import lt.fxportal.Repositories.ICurrencyRepository;
import lt.fxportal.Repositories.IExchangeRateRepository;


@Service
@AllArgsConstructor
public class ExchangeRateApplicationService {

    private static final String BASE_CURRENCY = "EUR";

    private final IExchangeRateRepository exchangeRateRepository;
    private final ICurrencyRepository currencyRepository;

    private final ExchangeRateMapper exchangeRateMapper;
    private final CurrencyConversionMapper currencyConversionMapper;
    private final CurrencyConverter currencyConverter;

    private final LbDataImportService lbDataImportService;

    @Transactional
    public void fetchAndSaveCurrentRates() throws Exception {
        lbDataImportService.importExchangeRates();
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
        if (!currencyRepository.existsById(currency)) {
            throw new CurrencyNotFoundException("Currency not found: " + currency);
        }

        if (from != null && to != null && to.isBefore(from)) {
            throw new InvalidDateRangeException("'to' date cannot be before 'from' date");
        }

        List<ExchangeRate> rates = exchangeRateRepository.findByCurrencyAndOptionalDates(currency, from, to);
        return exchangeRateMapper.toDTOList(rates);
    }

    public CurrencyConversionDTO convertAmount(String from, String to, BigDecimal amount) {
        if (amount.signum() < 0) {
            throw new InvalidAmountException("Amount must be non-negative");
        }

        BigDecimal fromRate = getLatestRate(from);
        BigDecimal toRate = getLatestRate(to);

        BigDecimal rateUsed = currencyConverter.getRate(fromRate, toRate);
        BigDecimal convertedAmount = currencyConverter.convert(amount, rateUsed);

        return currencyConversionMapper.toDTO(from, to, amount, convertedAmount, rateUsed);
    }

    private BigDecimal getLatestRate(String currencyCode) {
        if (currencyCode.equalsIgnoreCase(BASE_CURRENCY)) {
            return BigDecimal.ONE;
        }
        return exchangeRateRepository.findTopByCurrency_CodeOrderByDateDesc(currencyCode)
            .map(ExchangeRate::getRate)
            .orElseThrow(() -> 
                new CurrencyNotFoundException("Currency not found: " + currencyCode));
    }
}
