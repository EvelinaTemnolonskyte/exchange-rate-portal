package lt.fxportal.applicationservices.ports;

public interface ReferenceDataImporter {
    void importCurrencies();

    void importExchangeRates();
}