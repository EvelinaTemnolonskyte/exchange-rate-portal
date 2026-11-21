package lt.fxportal.domain.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

class CurrencyConverterTest {

    private final CurrencyConverter converter = new CurrencyConverter();

    @Test
    void convert_multipliesAmountByRate() {
        // Arrange
        BigDecimal amount = new BigDecimal("100");
        BigDecimal rate = new BigDecimal("1.5");

        // Act
        BigDecimal result = converter.convert(amount, rate);

        // Assert
        assertThat(result).isEqualByComparingTo("150");
    }

    @Test
    void convert_roundsToSevenDecimalsHalfUp() {
        // Arrange
        BigDecimal amount = new BigDecimal("1");
        BigDecimal rate = new BigDecimal("0.123456789");

        // Act
        BigDecimal result = converter.convert(amount, rate);

        // Assert
        assertThat(result).isEqualTo(new BigDecimal("0.1234568"));
    }

    @Test
    void convert_returnsZeroForZeroAmount() {
        // Arrange
        BigDecimal rate = new BigDecimal("1.5");

        // Act
        BigDecimal result = converter.convert(BigDecimal.ZERO, rate);

        // Assert
        assertThat(result).isEqualByComparingTo("0");
    }

    @Test
    void convert_throwsWhenAmountIsNull() {
        // Arrange
        BigDecimal rate = BigDecimal.ONE;

        // Act + Assert
        assertThatThrownBy(() -> converter.convert(null, rate))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getRate_dividesToRateByFromRate() {
        // Arrange
        BigDecimal fromRate = new BigDecimal("2");
        BigDecimal toRate = new BigDecimal("4");

        // Act
        BigDecimal result = converter.getRate(fromRate, toRate);

        // Assert
        assertThat(result).isEqualByComparingTo("2");
    }

    @Test
    void getRate_roundsToSevenDecimals() {
        // Arrange
        BigDecimal fromRate = new BigDecimal("3");
        BigDecimal toRate = BigDecimal.ONE;

        // Act
        BigDecimal result = converter.getRate(fromRate, toRate);

        // Assert
        assertThat(result).isEqualTo(new BigDecimal("0.3333333"));
    }

    @Test
    void getRate_throwsWhenFromRateIsZero() {
        // Arrange
        BigDecimal toRate = BigDecimal.ONE;

        // Act + Assert
        assertThatThrownBy(() -> converter.getRate(BigDecimal.ZERO, toRate))
                .isInstanceOf(ArithmeticException.class);
    }

    @Test
    void getRate_throwsWhenRateIsNull() {
        // Act + Assert
        assertThatThrownBy(() -> converter.getRate(null, BigDecimal.ONE))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
