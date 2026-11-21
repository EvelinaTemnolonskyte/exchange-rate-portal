package lt.fxportal.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Locale;
import java.util.List;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lt.fxportal.application.exception.CurrencyNotFoundException;
import lt.fxportal.application.exception.InvalidAmountException;
import lt.fxportal.application.exception.InvalidDateRangeException;
import lt.fxportal.application.port.ReferenceDataImporter;
import lt.fxportal.domain.CurrencyCodes;
import lt.fxportal.domain.entity.ExchangeRate;
import lt.fxportal.domain.service.CurrencyConverter;
import lt.fxportal.api.mapper.CurrencyConversionMapper;

import lt.fxportal.api.mapper.ExchangeRateMapper;
import lt.fxportal.infrastructure.repository.ICurrencyRepository;
import lt.fxportal.infrastructure.repository.IExchangeRateRepository;
import lt.fxportal.api.dto.CurrencyConversionDTO;
import lt.fxportal.api.dto.ExchangeRateDTO;


@Service
@AllArgsConstructor
public class ExchangeRateApplicationService {

    private final IExchangeRateRepository exchangeRateRepository;
    private final ICurrencyRepository currencyRepository;

    private final ExchangeRateMapper exchangeRateMapper;
    private final CurrencyConversionMapper currencyConversionMapper;
    private final CurrencyConverter currencyConverter;

    private final ReferenceDataImporter referenceDataImporter;

    public void importExchangeRates() {
        referenceDataImporter.importExchangeRates();
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
        String normalizedCurrency = normalizeCurrencyCode(currency);
        if (!currencyRepository.existsById(normalizedCurrency)) {
            throw new CurrencyNotFoundException("Currency not found: " + currency);
        }

        if (from != null && to != null && to.isBefore(from)) {
            throw new InvalidDateRangeException("'to' date cannot be before 'from' date");
        }

        List<ExchangeRate> rates = exchangeRateRepository.findByCurrencyAndOptionalDates(normalizedCurrency, from, to);
        return exchangeRateMapper.toDTOList(rates);
    }

    public CurrencyConversionDTO convertAmount(String from, String to, BigDecimal amount) {
        if (amount.signum() < 0) {
            throw new InvalidAmountException("Amount must be non-negative");
        }

        String normalizedFrom = normalizeCurrencyCode(from);
        String normalizedTo = normalizeCurrencyCode(to);
        BigDecimal fromRate = getLatestRate(normalizedFrom);
        BigDecimal toRate = getLatestRate(normalizedTo);

        BigDecimal rateUsed = currencyConverter.getRate(fromRate, toRate);
        BigDecimal convertedAmount = currencyConverter.convert(amount, rateUsed);

        return currencyConversionMapper.toDTO(normalizedFrom, normalizedTo, amount, convertedAmount, rateUsed);
    }

    private BigDecimal getLatestRate(String currencyCode) {
        String normalizedCurrencyCode = normalizeCurrencyCode(currencyCode);
        if (normalizedCurrencyCode.equals(CurrencyCodes.BASE_CURRENCY)) {
            return BigDecimal.ONE;
        }
        return exchangeRateRepository.findTopByCurrency_CodeOrderByDateDesc(normalizedCurrencyCode)
            .map(ExchangeRate::getRate)
            .orElseThrow(() -> 
                new CurrencyNotFoundException("Currency not found: " + normalizedCurrencyCode));
    }

    private String normalizeCurrencyCode(String currencyCode) {
        return currencyCode.toUpperCase(Locale.ROOT);
    }
}
