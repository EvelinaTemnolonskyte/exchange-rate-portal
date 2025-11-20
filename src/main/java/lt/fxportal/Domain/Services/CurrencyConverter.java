package lt.fxportal.Domain.Services;

import java.math.BigDecimal;
import java.math.RoundingMode;

import org.springframework.stereotype.Service;

import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class CurrencyConverter {

    public BigDecimal convert(BigDecimal amount, BigDecimal rate) {
        if (amount == null || rate == null) {
            throw new IllegalArgumentException("Amount and rate cannot be null");
        }
        return amount.multiply(rate).setScale(7, RoundingMode.HALF_UP);
    }

    public BigDecimal getRate(BigDecimal fromRate, BigDecimal toRate) {
        if (fromRate == null || toRate == null) {
            throw new IllegalArgumentException("Rates cannot be null");
        }
        return toRate.divide(fromRate, 7, RoundingMode.HALF_UP); 
    }
}