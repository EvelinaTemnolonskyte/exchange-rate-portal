package lt.fxportal.infrastructure.lithuanianbank.dto;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;

import lombok.Data;

@Data
public class CurrencyName {

    @JacksonXmlProperty(isAttribute = true, localName = "lang")
    private String languageCode;

    @JacksonXmlText
    private String localizedName;
}