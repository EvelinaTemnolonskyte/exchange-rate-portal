package lt.fxportal.Domain.Entities;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlText;
import lombok.Data;

@Data
public class CurrencyName {

    @JacksonXmlProperty(isAttribute = true, localName = "lang")
    private String lang;

    @JacksonXmlText
    private String value; 
}

