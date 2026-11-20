export type RateHistoryItem = {
  date: string;
  rate: number;
};

export type Currency = {
  code: string;
  englishName: string;
  numericCode: string | null;
  minorUnitDigits: number | null;
};

export type ExchangeRate = {
  currencyCode: string;
  date: string;
  rate: number;
};

export type CurrencyConversion = {
  fromCurrency: string;
  toCurrency: string;
  originalAmount: number;
  convertedAmount: number;
  rateUsed: number;
};