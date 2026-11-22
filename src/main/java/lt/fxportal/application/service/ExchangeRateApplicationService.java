package lt.fxportal.application.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lt.fxportal.api.dto.CurrencyConversionDTO;
import lt.fxportal.api.dto.ExchangeRateDTO;
import lt.fxportal.api.mapper.CurrencyConversionMapper;
import lt.fxportal.api.mapper.ExchangeRateMapper;
import lt.fxportal.application.exception.CurrencyNotFoundException;
import lt.fxportal.application.exception.ExchangeRateNotFoundException;
import lt.fxportal.application.exception.InvalidAmountException;
import lt.fxportal.application.exception.InvalidDateRangeException;
import lt.fxportal.application.port.ReferenceDataImporter;
import lt.fxportal.domain.CurrencyCodes;
import lt.fxportal.domain.entity.ExchangeRate;
import lt.fxportal.domain.service.CurrencyConverter;
import lt.fxportal.infrastructure.repository.ICurrencyRepository;
import lt.fxportal.infrastructure.repository.IExchangeRateRepository;

@Service
@RequiredArgsConstructor
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

    @Transactional(readOnly = true)
    public List<ExchangeRateDTO> getAllRates() {
        return exchangeRateMapper.toDTOList(exchangeRateRepository.findAll());
    }

    @Transactional(readOnly = true)
    public List<ExchangeRateDTO> getLatestRatesForAllCurrencies() {
        return exchangeRateMapper.toDTOList(exchangeRateRepository.findLatestRatesForAllCurrencies());
    }

    @Transactional(readOnly = true)
    public List<ExchangeRateDTO> getRatesForCurrency(String currency, LocalDate from, LocalDate to) {
        if (from != null && to != null && to.isBefore(from)) {
            throw new InvalidDateRangeException("'to' date cannot be before 'from' date");
        }

        String code = normalizeCurrencyCode(currency);
        if (!currencyRepository.existsById(code)) {
            throw new CurrencyNotFoundException("Currency not found: " + code);
        }

        return exchangeRateMapper.toDTOList(
                exchangeRateRepository.findByCurrencyAndOptionalDates(code, from, to));
    }

    @Transactional(readOnly = true)
    public CurrencyConversionDTO convertAmount(String from, String to, BigDecimal amount) {
        if (amount.signum() < 0) {
            throw new InvalidAmountException("Amount must be non-negative");
        }

        String fromCode = normalizeCurrencyCode(from);
        String toCode = normalizeCurrencyCode(to);

        BigDecimal rateUsed = currencyConverter.getRate(getLatestRate(fromCode), getLatestRate(toCode));
        BigDecimal convertedAmount = currencyConverter.convert(amount, rateUsed);

        return currencyConversionMapper.toDTO(fromCode, toCode, amount, convertedAmount, rateUsed);
    }

    private BigDecimal getLatestRate(String code) {
        if (CurrencyCodes.BASE_CURRENCY.equals(code)) {
            return BigDecimal.ONE;
        }
        return exchangeRateRepository.findTopByCurrency_CodeOrderByDateDesc(code)
                .map(ExchangeRate::getRate)
                .orElseThrow(() -> currencyRepository.existsById(code)
                        ? new ExchangeRateNotFoundException("No exchange rate available for: " + code)
                        : new CurrencyNotFoundException("Currency not found: " + code));
    }

    private String normalizeCurrencyCode(String code) {
        return code.toUpperCase(Locale.ROOT);
    }
}