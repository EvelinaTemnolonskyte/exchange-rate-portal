package lt.fxportal.api.dtos.lithuanianbankfxrates;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "CcyTbl")
public class CurrencyList {

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "CcyNtry")
    private List<CurrencyData> currencyEntries;
}