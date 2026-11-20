export type RateHistoryItem = {
  date: string;
  rate: number;
};

export type Currency = {
  code: string;
  nameEn: string;
  numCode: string;
  decimals: number;
};

export type ExchangeRate = {
  currency: string;
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