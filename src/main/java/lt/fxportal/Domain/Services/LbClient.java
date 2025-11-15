package lt.fxportal.Domain.Services;

import java.time.LocalDate;
import java.util.List;
import java.util.Collections;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyData;
import lt.fxportal.API.DTOs.lbLtFxRates.CurrencyList;
import lt.fxportal.API.DTOs.lbLtFxRates.DailyFxRate;
import lt.fxportal.API.DTOs.lbLtFxRates.DailyFxRateList;


@Service
public class LbClient {

    private static final String BASE_URL = "https://www.lb.lt/webservices/FxRates/FxRates.asmx";
    private final RestTemplate restTemplate;
    private final XmlMapper xmlMapper;

    public LbClient(RestTemplate restTemplate, XmlMapper xmlMapper) {
        this.restTemplate = restTemplate;
        this.xmlMapper = xmlMapper;
    }

    
    public List<CurrencyData> getCurrencyListXml() throws Exception {
        String url = BASE_URL + "/getCurrencyList";
        String xml = restTemplate.getForObject(url, String.class);
        CurrencyList currencyList = xmlMapper.readValue(xml, CurrencyList.class);
        return currencyList.getCcyNtry();
    }

    public List<DailyFxRate> getCurrentFxRatesXml() throws Exception {
        String url = BASE_URL + "/getCurrentFxRates?tp=EU";
        String xml = restTemplate.getForObject(url, String.class);

        DailyFxRateList dailyFxRateList = xmlMapper.readValue(xml, DailyFxRateList.class);

        return dailyFxRateList.getDailyFxRates();
    }

    public List<DailyFxRate> getFxRatesForCurrency(String currencyCode, LocalDate from, LocalDate to) throws Exception {
        String url = String.format("%s/getFxRatesForCurrency?tp=EU&ccy=%s&dtFrom=%s&dtTo=%s",
                BASE_URL, currencyCode, from.toString(), to.toString());
        System.out.println("Fetching FX rates from URL: " + url);
        String xml = restTemplate.getForObject(url, String.class);

        if (xml.contains("No data")) {
            System.out.println("No data found for currency " + currencyCode + ". Skipping.");
            return Collections.emptyList();
        }

        DailyFxRateList dailyFxRateList = xmlMapper.readValue(xml, DailyFxRateList.class);
        return dailyFxRateList.getDailyFxRates();
    }

    
}
