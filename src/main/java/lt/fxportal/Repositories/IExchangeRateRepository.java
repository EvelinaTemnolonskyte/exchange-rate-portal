package lt.fxportal.Repositories;
import java.time.LocalDate;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import lt.fxportal.Domain.Entities.ExchangeRate;

public interface IExchangeRateRepository extends JpaRepository<ExchangeRate, UUID>{
    boolean existsByCurrency_CodeAndDate(String currencyCode, LocalDate date);
}
