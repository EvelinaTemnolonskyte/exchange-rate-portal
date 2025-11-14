package lt.fxportal.Domain.Entities;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;

import lombok.Data;

@Data
public class CurrencyAmount {

    @JacksonXmlProperty(localName = "Ccy")
    private String currencyCode;

    @JacksonXmlProperty(localName = "Amt")
    private double amount;
}

