package lt.fxportal.API.DTOs;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CurrencyDTO {
    @NotNull
    private String code;

    @NotNull
    private String nameEn;

    @NotNull
    private String numCode;

    @NotNull
    private Integer decimals;
}