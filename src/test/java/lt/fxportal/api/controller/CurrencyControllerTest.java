package lt.fxportal.api.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import lt.fxportal.api.dto.CurrencyDTO;
import lt.fxportal.api.exception.GlobalExceptionHandler;
import lt.fxportal.application.exception.CurrencyNotFoundException;
import lt.fxportal.application.service.CurrencyApplicationService;

@WebMvcTest(CurrencyController.class)
@Import(GlobalExceptionHandler.class)
class CurrencyControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    CurrencyApplicationService currencyApplicationService;

    @Test
    void getCurrencies_returnsAllCurrencies() throws Exception {
        // Arrange
        when(currencyApplicationService.getAllCurrencies())
                .thenReturn(List.of(new CurrencyDTO("USD", "US dollar", "840", 2)));

        // Act + Assert
        mockMvc.perform(get("/currencies"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].code").value("USD"));
    }

    @Test
    void getCurrencies_withRatesTrue_callsCurrenciesWithRates() throws Exception {
        // Arrange
        when(currencyApplicationService.getCurrenciesWithRates()).thenReturn(List.of());

        // Act
        mockMvc.perform(get("/currencies").param("withRates", "true"))
                .andExpect(status().isOk());

        // Assert
        verify(currencyApplicationService).getCurrenciesWithRates();
    }

    @Test
    void getCurrencyByCode_returnsCurrency() throws Exception {
        // Arrange
        when(currencyApplicationService.getCurrencyByCode("USD"))
                .thenReturn(new CurrencyDTO("USD", "US dollar", "840", 2));

        // Act + Assert
        mockMvc.perform(get("/currencies/USD"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.englishName").value("US dollar"));
    }

    @Test
    void getCurrencyByCode_returns404WhenNotFound() throws Exception {
        // Arrange
        when(currencyApplicationService.getCurrencyByCode("XYZ"))
                .thenThrow(new CurrencyNotFoundException("Currency not found: XYZ"));

        // Act + Assert
        mockMvc.perform(get("/currencies/XYZ"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getCurrencyByCode_returns400WhenCodeInvalid() throws Exception {
        // Act + Assert
        mockMvc.perform(get("/currencies/US"))
                .andExpect(status().isBadRequest());
    }
}