package lt.fxportal.Domain.Services;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lt.fxportal.API.DTOs.ExchangeRateDTO;
import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyData;
import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyList;
import lt.fxportal.Domain.Entities.DailyFxRateList;

@Service
public class LbClient {

    private static final String BASE_URL = "https://www.lb.lt/webservices/FxRates/FxRates.asmx";
    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;

    public LbClient() {
        this.restTemplate = new RestTemplate();
        this.xmlMapper = new XmlMapper();
    }

    
    public List<CurrencyData> getCurrencyListXml() throws Exception {
        String url = BASE_URL + "/getCurrencyList";
        String xml = restTemplate.getForObject(url, String.class);
        CurrencyList currencyList = xmlMapper.readValue(xml, CurrencyList.class);
        return currencyList.getCcyNtry();
    }

    public List<ExchangeRateDTO> getCurrentRates() throws Exception {
        String url = BASE_URL + "/getCurrentFxRates?tp=EU";
        String xml = restTemplate.getForObject(url, String.class);

        DailyFxRateList dailyFxRateList = xmlMapper.readValue(xml, DailyFxRateList.class);

        return dailyFxRateList.getDailyFxRates().stream()
            .flatMap(daily -> daily.getCurrencyAmounts().stream()
                .filter(ca -> !ca.getCurrencyCode().equals("EUR")) // skip EUR as base
                .map(ca -> ExchangeRateDTO.builder()
                        .currencyCode(ca.getCurrencyCode())
                        .rate(ca.getAmount())
                        .date(daily.getDate())
                        .build()
                )
            )
            .toList();
    }
}
