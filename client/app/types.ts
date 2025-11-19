export type RateHistoryItem = {
  date: string;
  rate: number;
};

export type Currency = {
  code: string;
  name: string;
};

export type LatestRateItem = {
  currency: string;
  rate: number;
};