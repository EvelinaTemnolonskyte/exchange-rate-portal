package lt.fxportal.InfrastructureServices;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyAmount;
import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyData;
import lt.fxportal.API.DTOs.lbLtFxRates.DailyFxRate;
import lt.fxportal.Domain.Entities.Currency;
import lt.fxportal.Domain.Entities.ExchangeRate;
import lt.fxportal.Mappers.lbLtFxRates.LbCurrencyMapper;
import lt.fxportal.Mappers.lbLtFxRates.LbExchangeRateMapper;
import lt.fxportal.Repositories.ICurrencyRepository;
import lt.fxportal.Repositories.IExchangeRateRepository;

@Service
@AllArgsConstructor
public class LbDataImportService {

    private static final Logger log = LoggerFactory.getLogger(LbDataImportService.class);

    private final LbClient lbClient;
    private final LbExchangeRateMapper lbExchangeRateMapper;
    private final LbCurrencyMapper lbCurrencyMapper;
    private final IExchangeRateRepository exchangeRateRepository;
    private final ICurrencyRepository currencyRepository;

    @Transactional
    public void importExchangeRates() throws Exception {
        List<DailyFxRate> externalRates = lbClient.getCurrentFxRatesXml();
        List<ExchangeRate> entities = new ArrayList<>();
        for (DailyFxRate dailyRate : externalRates) {
            for (CurrencyAmount currencyAmount : dailyRate.getCurrencyAmounts()) {
                String currencyCode = currencyAmount.getCurrencyCode();
                if ("EUR".equals(currencyCode)) {
                    continue;
                }

                currencyRepository.findById(currencyCode).ifPresentOrElse(
                        currency -> entities.add(lbExchangeRateMapper.toEntity(
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
        List<CurrencyData> externalCurrencies = lbClient.getCurrencyListXml();
        List<Currency> entities = externalCurrencies.stream()
            .map(lbCurrencyMapper::toEntity)
            .filter(c -> !currencyRepository.existsById(c.getCode()))
            .toList();

        if (!entities.isEmpty()) {
            currencyRepository.saveAll(entities);
        }
    }
}