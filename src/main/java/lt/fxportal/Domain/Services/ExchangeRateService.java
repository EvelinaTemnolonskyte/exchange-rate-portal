package lt.fxportal.Domain.Services;

import java.util.List;

import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lt.fxportal.API.DTOs.ExchangeRateDTO;
import lt.fxportal.Domain.Entities.ExchangeRate;
import lt.fxportal.Mappers.ExchangeRateMapper;
import lt.fxportal.Repositories.IExchangeRateRepository;

@Service
@AllArgsConstructor
public class ExchangeRateService {

    private final IExchangeRateRepository exchangeRateRepository;
    private final ExchangeRateMapper mapper;

    @Transactional
    public ExchangeRateDTO saveExchangeRate(@NotNull @Valid ExchangeRateDTO dto) {
        ExchangeRate entity = mapper.toExchangeRate(dto);
        ExchangeRate saved = exchangeRateRepository.save(entity);
        return mapper.toExchangeRateDTO(saved);
    }

    @Transactional
    public void saveAllRates(List<ExchangeRateDTO> dtos) {
        List<ExchangeRate> entities = dtos.stream()
            .map(mapper::toExchangeRate)
            .filter(rate -> !exchangeRateRepository.existsByCurrencyCodeAndDate(rate.getCurrencyCode(), rate.getDate()))
            .toList();

        exchangeRateRepository.saveAll(entities);
    }


    public List<ExchangeRateDTO> getAllRates() {
        return mapper.toExchangeRateDTOList(exchangeRateRepository.findAll());
    }
}