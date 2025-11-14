package lt.fxportal.Domain.Entities;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "CcyTbl")
public class CurrencyList {
    
    @JacksonXmlElementWrapper(useWrapping = false)
    List<Currency> CcyNtry; 
}
