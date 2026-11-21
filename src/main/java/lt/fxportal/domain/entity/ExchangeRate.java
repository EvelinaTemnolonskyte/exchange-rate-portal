package lt.fxportal.domain.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.UniqueConstraint;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(
    name = "exchange_rate_to_eur",
    indexes = {
        @Index(name = "idx_currency_code", columnList = "currency_code"),
        @Index(name = "idx_date", columnList = "date")
    },
    uniqueConstraints = {
        @UniqueConstraint(columnNames = {"currency_code", "date"})
    }
)
public class ExchangeRate {

    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "currency_code", nullable = false)
    private Currency currency;

    @Column(name = "date", nullable = false)
    private LocalDate date;

    @Column(name = "rate", nullable = false, precision = 20, scale = 7)
    private BigDecimal rate;
}