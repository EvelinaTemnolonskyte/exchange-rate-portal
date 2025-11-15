package lt.fxportal.Domain.Entities;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "CcyNtry")
public class CurrencyData {

    @JacksonXmlProperty(localName = "Ccy")
    private String code;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "CcyNm")
    private List<CurrencyName> names;


    @JacksonXmlProperty(localName = "CcyNbr")
    private String numCode;

    @JacksonXmlProperty(localName = "CcyMnrUnts")
    private int decimals; 

}