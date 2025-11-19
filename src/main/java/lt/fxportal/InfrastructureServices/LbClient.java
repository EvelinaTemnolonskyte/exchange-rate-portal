package lt.fxportal.InfrastructureServices;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(LbClient.class);


    public LbClient(RestTemplate restTemplate, XmlMapper xmlMapper) {
        this.restTemplate = restTemplate;
        this.xmlMapper = xmlMapper;
    }

    
    public List<CurrencyData> getCurrencyListXml() throws Exception {
        String url = BASE_URL + "/getCurrencyList";
        logger.info("Fetching currency list from URL: {}", url);

        String xml = restTemplate.getForObject(url, String.class);
        CurrencyList currencyList = xmlMapper.readValue(xml, CurrencyList.class);
        
        logger.info("Fetched {} currencies", currencyList.getCcyNtry().size());

        return currencyList.getCcyNtry();
    }

    public List<DailyFxRate> getCurrentFxRatesXml() throws Exception {
        String url = BASE_URL + "/getCurrentFxRates?tp=EU";
        logger.info("Fetching current FX rates from URL: {}", url);

        String xml = restTemplate.getForObject(url, String.class);
        DailyFxRateList dailyFxRateList = xmlMapper.readValue(xml, DailyFxRateList.class);
        
        logger.info("Fetched {} daily FX rates", dailyFxRateList.getDailyFxRates().size());
        return dailyFxRateList.getDailyFxRates();
    }

}
