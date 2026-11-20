'use client';

import { useEffect, useState } from "react";
import { Currency, ExchangeRate, RateHistoryItem } from "@/app/types";
import Header from "@/app/components/Header";
import CurrencySelect from "@/app/components/CurrencySelect";

export default function HistoryPage() {
  const [currencies, setCurrencies] = useState<Currency[]>([]);
  const [currenciesLoaded, setCurrenciesLoaded] = useState(false); 
  const [selectedCurrency, setSelectedCurrency] = useState("");
  const [fromDate, setFromDate] = useState("");
  const [toDate, setToDate] = useState("");
  const [history, setHistory] = useState<RateHistoryItem[]>([]);
  const [loading, setLoading] = useState(false);
  const today = new Date().toISOString().split("T")[0];

  useEffect(() => {
    const fetchCurrencies = async () => {
      try {
        const res = await fetch("/api/currencies?withRates=true");
        if (!res.ok) throw new Error(`Failed to fetch currencies: ${res.status}`);

        setCurrencies(await res.json() as Currency[]);
        setCurrenciesLoaded(true);
      } catch (err) {
        console.error(err);
        setCurrencies([]);
        setCurrenciesLoaded(false);
        alert("Unable to fetch currencies. Please try again later."); 
      }
    };
    fetchCurrencies();
  }, []);

  useEffect(() => {
    if (!currenciesLoaded) {
        setHistory([]);
        return;
    }

    if (!selectedCurrency) {
        setHistory([]);
        return;
    }

    const loadHistory = async () => {
      setLoading(true);
      try {
        const params = new URLSearchParams();
        if (fromDate) params.set("fromDate", fromDate);
        if (toDate) params.set("toDate", toDate);
        const query = params.toString();
        const url = `/api/currencies/${encodeURIComponent(selectedCurrency)}/rates${query ? `?${query}` : ""}`;

        const res = await fetch(url);
        if (!res.ok) {
          throw new Error(`Failed to fetch exchange rates: ${res.status} ${res.statusText}`);
        }

        const rates = await res.json() as ExchangeRate[];
        setHistory(rates.map(({ date, rate }) => ({ date, rate })) satisfies RateHistoryItem[]);
      } catch (err) {
        console.error("Error loading history:", err);
        setHistory([]);
      } finally {
        setLoading(false);
      }
    };
    loadHistory();
  },[selectedCurrency, fromDate, toDate, currenciesLoaded]);

  return (
    <main className="flex flex-1 flex-col">
      <Header title="Exchange Rate History" showBack={true} />

      <section className="p-8 max-w-4xl mx-auto">

        <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
          <CurrencySelect
              label="Currency"
              value={selectedCurrency}
              onChange={setSelectedCurrency}
              currencies={currencies.filter(c => c.code !== "EUR")}
          />

          <div>
              <label className="font-semibold">From</label>
              <input
                  type="date"
                  className="border p-2 rounded w-full mt-1"
                  value={fromDate}
                  max={toDate || today}
                  onChange={(e) => {
                      const v = e.target.value;
                      setFromDate(v);
                      if (toDate && v > toDate) setToDate(v);
                  }}
              />
          </div>

          <div>
              <label className="font-semibold">To</label>
              <input
                  type="date"
                  className="border p-2 rounded w-full mt-1"
                  value={toDate}
                  min={fromDate || undefined}
                  max={today}
                  onChange={(e) => {
                      const v = e.target.value;
                      setToDate(v);
                      if (fromDate && v < fromDate) setFromDate(v);
                  }}
              />
          </div>
        </div>

        {loading && <p>Loading history...</p>}

        {!loading && history.length === 0 && selectedCurrency && (
            <p className="text-center text-gray-500 mt-4">
                No data found for selected currency and dates.
            </p>
        )}

        {history.length > 0 && (
            <div className="flex justify-center items-center flex-col">
              <h2 className="text-xl font-semibold mb-4">
                  History for {selectedCurrency}
              </h2>

              <table className="w-full border">
                  <thead className="bg-gray-200">
                      <tr>
                          <th className="p-2 border">Date</th>
                          <th className="p-2 border">Rate (1 EUR = ...)</th>
                      </tr>
                  </thead>
                  <tbody>
                      {history.map((row, i) => (
                          <tr key={i}>
                              <td className="border p-2">{row.date}</td>
                              <td className="border p-2">{row.rate}</td>
                          </tr>
                      ))}
                  </tbody>
              </table>
            </div>
        )}
      </section>
    </main>
  );
}