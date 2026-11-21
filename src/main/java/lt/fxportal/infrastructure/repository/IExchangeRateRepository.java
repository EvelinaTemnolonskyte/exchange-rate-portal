package lt.fxportal.infrastructure.repository;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import lt.fxportal.domain.entity.ExchangeRate;

public interface IExchangeRateRepository extends JpaRepository<ExchangeRate, UUID>{
    boolean existsByCurrency_CodeAndDate(String currencyCode, LocalDate date);

    @Query("""
            SELECT e FROM ExchangeRate e
            WHERE e.date = (SELECT MAX(e2.date)
                            FROM ExchangeRate e2
                            WHERE e2.currency = e.currency)
            """)
    List<ExchangeRate> findLatestRatesForAllCurrencies();

    Optional<ExchangeRate> findTopByCurrency_CodeOrderByDateDesc(String currencyCode);

    List<ExchangeRate> findByDateInAndCurrency_CodeIn(
        Collection<LocalDate> dates,
        Collection<String> currencyCodes
    );

    @Query("""
            SELECT e FROM ExchangeRate e
            WHERE e.currency.code = :currency
            AND (:from IS NULL OR e.date >= :from)
            AND (:to IS NULL OR e.date <= :to)
            """)
    List<ExchangeRate> findByCurrencyAndOptionalDates(
        @Param("currency") String currency,
        @Param("from") LocalDate from,
        @Param("to") LocalDate to
    );

}
