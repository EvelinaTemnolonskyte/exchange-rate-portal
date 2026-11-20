package lt.fxportal.infrastructure.lithuanianbank;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lt.fxportal.application.exceptions.DataImportException;
import lt.fxportal.application.ports.ReferenceDataImporter;
import lt.fxportal.domain.CurrencyCodes;
import lt.fxportal.domain.entities.Currency;
import lt.fxportal.domain.entities.ExchangeRate;
import lt.fxportal.infrastructure.lithuanianbank.mapper.LithuanianBankCurrencyMapper;
import lt.fxportal.infrastructure.lithuanianbank.mapper.LithuanianBankExchangeRateMapper;
import lt.fxportal.infrastructure.lithuanianbank.client.LithuanianBankApiClient;
import lt.fxportal.repositories.ICurrencyRepository;
import lt.fxportal.repositories.IExchangeRateRepository;
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
    @Transactional
    public void importExchangeRates() {
        try {
            importExchangeRatesFromBank();
        } catch (Exception exception) {
            throw new DataImportException("Failed to import exchange rates from Lithuanian Bank", exception);
        }
    }

    private void importExchangeRatesFromBank() throws Exception {
        List<DailyFxRate> externalRates = lithuanianBankApiClient.getCurrentFxRatesXml();
        List<ExchangeRate> entities = new ArrayList<>();
        for (DailyFxRate dailyRate : externalRates) {
            for (CurrencyAmount currencyAmount : dailyRate.getCurrencyAmounts()) {
                String currencyCode = currencyAmount.getCurrencyCode();
                if (CurrencyCodes.BASE_CURRENCY.equals(currencyCode)) {
                    continue;
                }

                currencyRepository.findById(currencyCode).ifPresentOrElse(
                        currency -> entities.add(lithuanianBankExchangeRateMapper.toEntity(
                                currencyAmount, currency, LocalDate.parse(dailyRate.getDate()))),
                        () -> log.warn("Skipped unknown currency: {}", currencyCode));
            }
        }

        List<ExchangeRate> newRates = entities.stream()
            .filter(rate -> !exchangeRateRepository
                    .existsByCurrency_CodeAndDate(rate.getCurrency().getCode(), rate.getDate()))
            .toList();

        exchangeRateRepository.saveAll(newRates);
    }

    @Override
    @Transactional
    public void importCurrencies() {
        try {
            importCurrenciesFromBank();
        } catch (Exception exception) {
            throw new DataImportException("Failed to import currencies from Lithuanian Bank", exception);
        }
    }

    private void importCurrenciesFromBank() throws Exception {
        List<CurrencyData> externalCurrencies = lithuanianBankApiClient.getCurrencyListXml();
        List<Currency> entities = externalCurrencies.stream()
            .map(lithuanianBankCurrencyMapper::toEntity)
            .filter(c -> !currencyRepository.existsById(c.getCode()))
            .toList();

        if (!entities.isEmpty()) {
            currencyRepository.saveAll(entities);
        }
    }
}