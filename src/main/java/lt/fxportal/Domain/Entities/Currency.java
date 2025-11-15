package lt.fxportal.Domain.Entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "currencies")
public class Currency {

    @Id
    @Column(name = "code", nullable = false, length = 3)
    private String code;

    @Column(name = "name_en", nullable = false)
    private String nameEn;

    @Column(name = "num_code")
    private String numCode;

    @Column(name = "decimals")
    private Integer decimals;
}