package lt.fxportal.application.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

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

@ExtendWith(MockitoExtension.class)
class ExchangeRateApplicationServiceTest {

    @Mock
    IExchangeRateRepository exchangeRateRepository;

    @Mock
    ICurrencyRepository currencyRepository;

    @Mock
    ExchangeRateMapper exchangeRateMapper;

    @Mock
    CurrencyConversionMapper currencyConversionMapper;

    @Mock
    CurrencyConverter currencyConverter;

    @Mock
    ReferenceDataImporter referenceDataImporter;

    @InjectMocks
    ExchangeRateApplicationService exchangeRateApplicationService;

    @Test
    void importExchangeRates_delegatesToImporter() {
        // Act
        exchangeRateApplicationService.importExchangeRates();

        // Assert
        verify(referenceDataImporter).importExchangeRates();
    }

    @Test
    void getAllRates_returnsMappedRates() {
        // Arrange
        List<ExchangeRate> rates = List.of(new ExchangeRate());
        List<ExchangeRateDTO> expectedDtos = List.of(
                new ExchangeRateDTO("USD", LocalDate.of(2026, 1, 5), new BigDecimal("1.10")));

        when(exchangeRateRepository.findAll()).thenReturn(rates);
        when(exchangeRateMapper.toDTOList(rates)).thenReturn(expectedDtos);

        // Act
        List<ExchangeRateDTO> result = exchangeRateApplicationService.getAllRates();

        // Assert
        assertEquals(expectedDtos, result);
    }

    @Test
    void getLatestRatesForAllCurrencies_returnsMappedRates() {
        // Arrange
        List<ExchangeRate> rates = List.of(new ExchangeRate());
        List<ExchangeRateDTO> expectedDtos = List.of(
                new ExchangeRateDTO("USD", LocalDate.of(2026, 1, 5), new BigDecimal("1.10")));

        when(exchangeRateRepository.findLatestRatesForAllCurrencies()).thenReturn(rates);
        when(exchangeRateMapper.toDTOList(rates)).thenReturn(expectedDtos);

        // Act
        List<ExchangeRateDTO> result = exchangeRateApplicationService.getLatestRatesForAllCurrencies();

        // Assert
        assertEquals(expectedDtos, result);
    }

    @Test
    void getRatesForCurrency_returnsMappedRates_whenValidRequest() {
        // Arrange
        LocalDate from = LocalDate.of(2026, 1, 1);
        LocalDate to = LocalDate.of(2026, 1, 10);
        List<ExchangeRate> rates = List.of(new ExchangeRate());
        List<ExchangeRateDTO> expectedDtos = List.of(
                new ExchangeRateDTO("USD", LocalDate.of(2026, 1, 5), new BigDecimal("1.10")));

        when(currencyRepository.existsById("USD")).thenReturn(true);
        when(exchangeRateRepository.findByCurrencyAndOptionalDates("USD", from, to)).thenReturn(rates);
        when(exchangeRateMapper.toDTOList(rates)).thenReturn(expectedDtos);

        // Act
        List<ExchangeRateDTO> result = exchangeRateApplicationService.getRatesForCurrency("usd", from, to);

        // Assert
        assertEquals(expectedDtos, result);
        verify(currencyRepository).existsById("USD");
        verify(exchangeRateRepository).findByCurrencyAndOptionalDates("USD", from, to);
    }

    @Test
    void getRatesForCurrency_throwsCurrencyNotFoundException_whenCurrencyDoesNotExist() {
        // Arrange
        when(currencyRepository.existsById("XYZ")).thenReturn(false);

        // Act + Assert
        assertThrows(CurrencyNotFoundException.class,
                () -> exchangeRateApplicationService.getRatesForCurrency("xyz", null, null));
        verifyNoInteractions(exchangeRateRepository);
    }

    @Test
    void getRatesForCurrency_throwsInvalidDateRangeException_whenToIsBeforeFrom() {
        // Arrange
        LocalDate from = LocalDate.of(2026, 1, 10);
        LocalDate to = LocalDate.of(2026, 1, 1);

        // Act + Assert
        assertThrows(InvalidDateRangeException.class,
                () -> exchangeRateApplicationService.getRatesForCurrency("USD", from, to));
        verifyNoInteractions(currencyRepository, exchangeRateRepository);
    }

    @Test
    void convertAmount_convertsSuccessfully_betweenTwoNonBaseCurrencies() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        BigDecimal usdRateValue = new BigDecimal("1.10");
        BigDecimal gbpRateValue = new BigDecimal("0.85");
        BigDecimal rateUsed = new BigDecimal("0.7727");
        BigDecimal convertedAmount = new BigDecimal("77.27");

        ExchangeRate usdRate = new ExchangeRate();
        usdRate.setRate(usdRateValue);
        ExchangeRate gbpRate = new ExchangeRate();
        gbpRate.setRate(gbpRateValue);

        CurrencyConversionDTO expectedDto = new CurrencyConversionDTO(
                "USD", "GBP", amount, convertedAmount, rateUsed);

        when(exchangeRateRepository.findTopByCurrency_CodeOrderByDateDesc("USD"))
                .thenReturn(Optional.of(usdRate));
        when(exchangeRateRepository.findTopByCurrency_CodeOrderByDateDesc("GBP"))
                .thenReturn(Optional.of(gbpRate));
        when(currencyConverter.getRate(usdRateValue, gbpRateValue)).thenReturn(rateUsed);
        when(currencyConverter.convert(amount, rateUsed)).thenReturn(convertedAmount);
        when(currencyConversionMapper.toDTO("USD", "GBP", amount, convertedAmount, rateUsed))
                .thenReturn(expectedDto);

        // Act
        CurrencyConversionDTO result = exchangeRateApplicationService.convertAmount("usd", "gbp", amount);

        // Assert
        assertEquals(expectedDto, result);
    }

    @Test
    void convertAmount_usesBigDecimalOne_whenConvertingFromBaseCurrency() {
        // Arrange
        String baseCurrency = CurrencyCodes.BASE_CURRENCY;
        BigDecimal amount = new BigDecimal("50.00");
        BigDecimal usdRateValue = new BigDecimal("1.10");
        BigDecimal rateUsed = new BigDecimal("1.10");
        BigDecimal convertedAmount = new BigDecimal("55.00");

        ExchangeRate usdRate = new ExchangeRate();
        usdRate.setRate(usdRateValue);

        CurrencyConversionDTO expectedDto = new CurrencyConversionDTO(
                baseCurrency, "USD", amount, convertedAmount, rateUsed);

        when(exchangeRateRepository.findTopByCurrency_CodeOrderByDateDesc("USD"))
                .thenReturn(Optional.of(usdRate));
        when(currencyConverter.getRate(BigDecimal.ONE, usdRateValue)).thenReturn(rateUsed);
        when(currencyConverter.convert(amount, rateUsed)).thenReturn(convertedAmount);
        when(currencyConversionMapper.toDTO(baseCurrency, "USD", amount, convertedAmount, rateUsed))
                .thenReturn(expectedDto);

        // Act
        CurrencyConversionDTO result = exchangeRateApplicationService.convertAmount(baseCurrency, "USD", amount);

        // Assert
        assertEquals(expectedDto, result);
        verify(currencyConverter).getRate(BigDecimal.ONE, usdRateValue);
    }

    @Test
    void convertAmount_throwsInvalidAmountException_whenAmountIsNegative() {
        // Arrange
        BigDecimal negativeAmount = new BigDecimal("-10.00");

        // Act + Assert
        assertThrows(InvalidAmountException.class,
                () -> exchangeRateApplicationService.convertAmount("USD", "EUR", negativeAmount));
        verifyNoInteractions(exchangeRateRepository, currencyConverter);
    }

    @Test
    void convertAmount_throwsCurrencyNotFoundException_whenCurrencyDoesNotExist() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        when(exchangeRateRepository.findTopByCurrency_CodeOrderByDateDesc("XYZ"))
                .thenReturn(Optional.empty());
        when(currencyRepository.existsById("XYZ")).thenReturn(false);

        // Act + Assert
        assertThrows(CurrencyNotFoundException.class,
                () -> exchangeRateApplicationService.convertAmount("XYZ", "USD", amount));
        verifyNoInteractions(currencyConverter);
    }

    @Test
    void convertAmount_throwsExchangeRateNotFoundException_whenCurrencyExistsButHasNoRate() {
        // Arrange
        BigDecimal amount = new BigDecimal("100.00");
        when(exchangeRateRepository.findTopByCurrency_CodeOrderByDateDesc("XYZ"))
                .thenReturn(Optional.empty());
        when(currencyRepository.existsById("XYZ")).thenReturn(true);

        // Act + Assert
        assertThrows(ExchangeRateNotFoundException.class,
                () -> exchangeRateApplicationService.convertAmount("XYZ", "USD", amount));
        verifyNoInteractions(currencyConverter);
    }
}