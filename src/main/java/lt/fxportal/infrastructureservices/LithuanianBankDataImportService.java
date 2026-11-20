package lt.fxportal.infrastructureservices;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lt.fxportal.domain.entities.Currency;
import lt.fxportal.domain.entities.ExchangeRate;
import lt.fxportal.mappers.lithuanianbankfxrates.LithuanianBankCurrencyMapper;
import lt.fxportal.mappers.lithuanianbankfxrates.LithuanianBankExchangeRateMapper;
import lt.fxportal.repositories.ICurrencyRepository;
import lt.fxportal.repositories.IExchangeRateRepository;
import lt.fxportal.api.dtos.lithuanianbankfxrates.CurrencyAmount;
import lt.fxportal.api.dtos.lithuanianbankfxrates.CurrencyData;
import lt.fxportal.api.dtos.lithuanianbankfxrates.DailyFxRate;

@Service
@AllArgsConstructor
public class LithuanianBankDataImportService {

    private static final Logger log = LoggerFactory.getLogger(LithuanianBankDataImportService.class);

    private final LithuanianBankApiClient lithuanianBankApiClient;
    private final LithuanianBankExchangeRateMapper lithuanianBankExchangeRateMapper;
    private final LithuanianBankCurrencyMapper lithuanianBankCurrencyMapper;
    private final IExchangeRateRepository exchangeRateRepository;
    private final ICurrencyRepository currencyRepository;

    @Transactional
    public void importExchangeRates() throws Exception {
        List<DailyFxRate> externalRates = lithuanianBankApiClient.getCurrentFxRatesXml();
        List<ExchangeRate> entities = new ArrayList<>();
        for (DailyFxRate dailyRate : externalRates) {
            for (CurrencyAmount currencyAmount : dailyRate.getCurrencyAmounts()) {
                String currencyCode = currencyAmount.getCurrencyCode();
                if ("EUR".equals(currencyCode)) {
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

    @Transactional
    public void importCurrencies() throws Exception {
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