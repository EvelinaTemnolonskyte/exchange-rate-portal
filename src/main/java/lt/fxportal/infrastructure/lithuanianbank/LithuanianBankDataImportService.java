package lt.fxportal.infrastructure.lithuanianbank;

import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;
import lt.fxportal.application.exception.DataImportException;
import lt.fxportal.application.port.ReferenceDataImporter;
import lt.fxportal.domain.CurrencyCodes;
import lt.fxportal.domain.entity.Currency;
import lt.fxportal.domain.entity.ExchangeRate;
import lt.fxportal.infrastructure.lithuanianbank.mapper.LithuanianBankCurrencyMapper;
import lt.fxportal.infrastructure.lithuanianbank.mapper.LithuanianBankExchangeRateMapper;
import lt.fxportal.infrastructure.lithuanianbank.client.LithuanianBankApiClient;
import lt.fxportal.infrastructure.repository.ICurrencyRepository;
import lt.fxportal.infrastructure.repository.IExchangeRateRepository;
import lt.fxportal.infrastructure.lithuanianbank.dto.CurrencyAmount;
import lt.fxportal.infrastructure.lithuanianbank.dto.CurrencyData;
import lt.fxportal.infrastructure.lithuanianbank.dto.DailyFxRate;

@Service
@AllArgsConstructor
public class LithuanianBankDataImportService implements ReferenceDataImporter {

    private static final Logger log = LoggerFactory.getLogger(LithuanianBankDataImportService.class);

    private final LithuanianBankApiClient lithuanianBankApiClient;
    private final LithuanianBankExchangeRateMapper lithuanianBankExchangeRateMapper;
    private final LithuanianBankCurrencyMapper lithuanianBankCurrencyMapper;
    private final IExchangeRateRepository exchangeRateRepository;
    private final ICurrencyRepository currencyRepository;

    @Override
    public void importExchangeRates() {
        try {
            importExchangeRatesFromBank();
        } catch (Exception exception) {
            throw new DataImportException("Failed to import exchange rates from Lithuanian Bank", exception);
        }
    }

    private void importExchangeRatesFromBank() throws Exception {
        List<DailyFxRate> externalRates = lithuanianBankApiClient.getCurrentFxRatesXml();
        Set<String> requestedCurrencyCodes = externalRates.stream()
                .flatMap(dailyRate -> dailyRate.getCurrencyAmounts().stream())
                .map(CurrencyAmount::getCurrencyCode)
                .filter(currencyCode -> !CurrencyCodes.BASE_CURRENCY.equals(currencyCode))
                .collect(Collectors.toCollection(LinkedHashSet::new));

        if (requestedCurrencyCodes.isEmpty()) {
            return;
        }

        Map<String, Currency> currenciesByCode = currencyRepository.findAllById(requestedCurrencyCodes).stream()
                .collect(Collectors.toMap(Currency::getCode, Function.identity(), (existing, duplicate) -> existing));

        Set<String> unknownCurrencyCodes = new LinkedHashSet<>(requestedCurrencyCodes);
        unknownCurrencyCodes.removeAll(currenciesByCode.keySet());
        unknownCurrencyCodes.forEach(currencyCode -> log.warn("Skipped unknown currency: {}", currencyCode));

        if (currenciesByCode.isEmpty()) {
            throw new IllegalStateException("No currency records found for the imported exchange rates; import currencies first");
        }

        Map<RateKey, ExchangeRate> candidateRates = new LinkedHashMap<>();
        for (DailyFxRate dailyRate : externalRates) {
            LocalDate rateDate = LocalDate.parse(dailyRate.getDate());
            for (CurrencyAmount currencyAmount : dailyRate.getCurrencyAmounts()) {
                String currencyCode = currencyAmount.getCurrencyCode();
                if (CurrencyCodes.BASE_CURRENCY.equals(currencyCode)) {
                    continue;
                }

                Currency currency = currenciesByCode.get(currencyCode);
                if (currency != null) {
                    RateKey key = new RateKey(currencyCode, rateDate);
                    candidateRates.putIfAbsent(key,
                            lithuanianBankExchangeRateMapper.toEntity(currencyAmount, currency, rateDate));
                }
            }
        }

        if (candidateRates.isEmpty()) {
            return;
        }

        List<LocalDate> dates = candidateRates.keySet().stream()
                .map(RateKey::date)
                .distinct()
                .toList();
        List<String> currencyCodes = candidateRates.keySet().stream()
                .map(RateKey::currencyCode)
                .distinct()
                .toList();
        Set<RateKey> existingRates = exchangeRateRepository
                .findByDateInAndCurrency_CodeIn(dates, currencyCodes)
                .stream()
                .map(rate -> new RateKey(rate.getCurrency().getCode(), rate.getDate()))
                .collect(Collectors.toSet());

        List<ExchangeRate> newRates = candidateRates.entrySet().stream()
                .filter(entry -> !existingRates.contains(entry.getKey()))
                .map(Map.Entry::getValue)
                .toList();

        if (!newRates.isEmpty()) {
            exchangeRateRepository.saveAll(newRates);
        }
    }

    @Override
    public void importCurrencies() {
        try {
            importCurrenciesFromBank();
        } catch (Exception exception) {
            throw new DataImportException("Failed to import currencies from Lithuanian Bank", exception);
        }
    }

    private void importCurrenciesFromBank() throws Exception {
        List<CurrencyData> externalCurrencies = lithuanianBankApiClient.getCurrencyListXml();
        Map<String, Currency> importedCurrencies = externalCurrencies.stream()
                .map(lithuanianBankCurrencyMapper::toEntity)
                .collect(Collectors.toMap(Currency::getCode, Function.identity(), (first, duplicate) -> first,
                        LinkedHashMap::new));

        if (importedCurrencies.isEmpty()) {
            return;
        }

        Set<String> existingCodes = currencyRepository.findAllById(importedCurrencies.keySet()).stream()
                .map(Currency::getCode)
                .collect(Collectors.toSet());
        List<Currency> newCurrencies = importedCurrencies.values().stream()
                .filter(currency -> !existingCodes.contains(currency.getCode()))
                .toList();

        if (!newCurrencies.isEmpty()) {
            currencyRepository.saveAll(newCurrencies);
        }
    }

    private record RateKey(String currencyCode, LocalDate date) {
    }
}