package lt.fxportal.applicationservices.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import lt.fxportal.applicationservices.models.CurrencyView;
import lt.fxportal.domain.entities.Currency;

@Mapper(componentModel = "spring")
public interface CurrencyViewMapper {
    CurrencyView toView(Currency currency);

    List<CurrencyView> toViewList(List<Currency> currencies);
}