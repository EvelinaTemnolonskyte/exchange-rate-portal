package lt.fxportal.api.dtos.lithuanianbankfxrates;

import java.util.List;

import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlElementWrapper;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlProperty;
import com.fasterxml.jackson.dataformat.xml.annotation.JacksonXmlRootElement;

import lombok.Data;

@Data
@JacksonXmlRootElement(localName = "FxRate")
public class DailyFxRate {

    @JacksonXmlProperty(localName = "Dt")
    private String date;

    @JacksonXmlProperty(localName = "Tp")
    private String rateType;

    @JacksonXmlElementWrapper(useWrapping = false)
    @JacksonXmlProperty(localName = "CcyAmt")
    private List<CurrencyAmount> currencyAmounts;
}