package lt.fxportal.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import lt.fxportal.api.dto.CurrencyDTO;
import lt.fxportal.api.mapper.CurrencyMapper;
import lt.fxportal.application.exception.CurrencyNotFoundException;
import lt.fxportal.application.port.ReferenceDataImporter;
import lt.fxportal.domain.entity.Currency;
import lt.fxportal.domain.entity.ExchangeRate;
import lt.fxportal.infrastructure.repository.ICurrencyRepository;
import lt.fxportal.infrastructure.repository.IExchangeRateRepository;

@ExtendWith(MockitoExtension.class)
class CurrencyApplicationServiceTest {

    @Mock ICurrencyRepository currencyRepository;
    @Mock IExchangeRateRepository exchangeRateRepository;
    @Mock CurrencyMapper currencyMapper;
    @Mock ReferenceDataImporter referenceDataImporter;

    @Captor ArgumentCaptor<List<Currency>> currenciesCaptor;

    @InjectMocks CurrencyApplicationService service;

    @Test
    void importCurrencies_delegatesToImporter() {
        // Act
        service.importCurrencies();

        // Assert
        verify(referenceDataImporter).importCurrencies();
    }

    @Test
    void getAllCurrencies_returnsMappedCurrencies() {
        // Arrange
        List<Currency> entities = List.of(currency("USD"));
        List<CurrencyDTO> dtos = List.of(new CurrencyDTO("USD", "US dollar", "840", 2));
        when(currencyRepository.findAll()).thenReturn(entities);
        when(currencyMapper.toDTOList(entities)).thenReturn(dtos);

        // Act
        List<CurrencyDTO> result = service.getAllCurrencies();

        // Assert
        assertThat(result).isEqualTo(dtos);
    }

    @Test
    void getCurrencyByCode_returnsDtoAndUppercasesCode() {
        // Arrange
        Currency usd = currency("USD");
        CurrencyDTO dto = new CurrencyDTO("USD", "US dollar", "840", 2);
        when(currencyRepository.findById("USD")).thenReturn(Optional.of(usd));
        when(currencyMapper.toDTO(usd)).thenReturn(dto);

        // Act
        CurrencyDTO result = service.getCurrencyByCode("usd");

        // Assert
        assertThat(result).isEqualTo(dto);
    }

    @Test
    void getCurrencyByCode_throwsWhenNotFound() {
        // Arrange
        when(currencyRepository.findById("XYZ")).thenReturn(Optional.empty());

        // Act + Assert
        assertThatThrownBy(() -> service.getCurrencyByCode("xyz"))
                .isInstanceOf(CurrencyNotFoundException.class)
                .hasMessageContaining("xyz");
    }

    @Test
    void getCurrenciesWithRates_addsBaseCurrencyWhenMissing() {
        // Arrange
        Currency usd = currency("USD");
        Currency eur = currency("EUR");
        when(exchangeRateRepository.findLatestRatesForAllCurrencies())
                .thenReturn(List.of(rate(usd)));
        when(currencyRepository.findAllById(List.of("USD")))
                .thenReturn(new ArrayList<>(List.of(usd))); 
        when(currencyRepository.findById("EUR")).thenReturn(Optional.of(eur));
        when(currencyMapper.toDTOList(anyList())).thenReturn(List.of());

        // Act
        service.getCurrenciesWithRates();

        // Assert
        verify(currencyMapper).toDTOList(currenciesCaptor.capture());
        assertThat(currenciesCaptor.getValue()).containsExactlyInAnyOrder(usd, eur);
    }

    @Test
    void getCurrenciesWithRates_doesNotDuplicateBaseCurrency() {
        // Arrange
        Currency eur = currency("EUR");
        when(exchangeRateRepository.findLatestRatesForAllCurrencies())
                .thenReturn(List.of(rate(eur)));
        when(currencyRepository.findAllById(List.of("EUR")))
                .thenReturn(new ArrayList<>(List.of(eur)));
        when(currencyRepository.findById("EUR")).thenReturn(Optional.of(eur));
        when(currencyMapper.toDTOList(anyList())).thenReturn(List.of());

        // Act
        service.getCurrenciesWithRates();

        // Assert
        verify(currencyMapper).toDTOList(currenciesCaptor.capture());
        assertThat(currenciesCaptor.getValue()).containsExactly(eur);
    }

    private Currency currency(String code) {
        return Currency.builder().code(code).build();
    }

    private ExchangeRate rate(Currency currency) {
        return ExchangeRate.builder().currency(currency).build();
    }
}