package lt.fxportal.API.DTOs;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CurrencyDTO {
    private String code;
    private String nameEn;
    private String numCode;
    private int decimals;
}