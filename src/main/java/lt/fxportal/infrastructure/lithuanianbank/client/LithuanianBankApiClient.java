package lt.fxportal.infrastructure.lithuanianbank.client;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import lt.fxportal.infrastructure.lithuanianbank.dto.CurrencyData;
import lt.fxportal.infrastructure.lithuanianbank.dto.CurrencyList;
import lt.fxportal.infrastructure.lithuanianbank.dto.DailyFxRate;
import lt.fxportal.infrastructure.lithuanianbank.dto.DailyFxRateList;

@Service
public class LithuanianBankApiClient {

    private static final Logger logger = LoggerFactory.getLogger(LithuanianBankApiClient.class);

    private final RestTemplate restTemplate;
    private final String baseUrl;

    private final XmlMapper xmlMapper = XmlMapper.builder()
            .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
            .build();

    public LithuanianBankApiClient(RestTemplate restTemplate,
                    @Value("${lithuanian-bank.api.base-url:https://www.lb.lt/webservices/FxRates/FxRates.asmx}") String baseUrl) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    public List<CurrencyData> getCurrencyListXml() throws Exception {
        String url = baseUrl + "/getCurrencyList";
        logger.info("Fetching currency list from URL: {}", url);

        String xml = restTemplate.getForObject(url, String.class);
        CurrencyList currencyList = xmlMapper.readValue(xml, CurrencyList.class);

        logger.info("Fetched {} currencies", currencyList.getCurrencyEntries().size());
        return currencyList.getCurrencyEntries();
    }

    public List<DailyFxRate> getCurrentFxRatesXml() throws Exception {
        String url = baseUrl + "/getCurrentFxRates?tp=EU";
        logger.info("Fetching current FX rates from URL: {}", url);

        String xml = restTemplate.getForObject(url, String.class);
        DailyFxRateList dailyFxRateList = xmlMapper.readValue(xml, DailyFxRateList.class);

        logger.info("Fetched {} daily FX rates", dailyFxRateList.getDailyFxRates().size());
        return dailyFxRateList.getDailyFxRates();
    }
}