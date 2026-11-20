package lt.fxportal.infrastructure.lithuanianbank.dto;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "CcyNtry")
public class CurrencyData {

    @JacksonXmlProperty(localName = "Ccy")
    private String currencyCode;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "CcyNm")
    private List<CurrencyName> localizedNames;

    @JacksonXmlProperty(localName = "CcyNbr")
    private String numericCode;

    @JacksonXmlProperty(localName = "CcyMnrUnts")
    private int minorUnitDigits;
}