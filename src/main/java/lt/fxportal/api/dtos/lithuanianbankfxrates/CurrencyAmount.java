package lt.fxportal.api.dtos.lithuanianbankfxrates;

import java.math.BigDecimal;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class CurrencyAmount {

    @JacksonXmlProperty(localName = "Ccy")
    private String currencyCode;

    @JacksonXmlProperty(localName = "Amt")
    private BigDecimal amount;
}