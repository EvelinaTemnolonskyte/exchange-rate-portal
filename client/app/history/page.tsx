'use client';

import { useEffect, useState } from "react";
import { Currency, RateHistoryItem } from "@/app/types";

export default function HistoryPage(){
    const [currencies, setCurrencies] = useState<Currency[]>([]);
    const [selectedCurrency, setSelectedCurrency] = useState("");
    const [fromDate, setFromDate] = useState("");
    const [toDate, setToDate] = useState("");
    const [history, setHistory] = useState<RateHistoryItem[]>([]);
    const [loading, setLoading] = useState(false);
    const today = new Date().toISOString().split("T")[0];

    useEffect(() => {
        fetch("/api/currencies")
            .then((res) => res.text())
            .then((xml) => {
                //console.log("Raw XML from backend:", xml);
                const parser = new DOMParser();
                const dom = parser.parseFromString(xml, "application/xml");
                const allCurrencies: Currency[] = [...dom.getElementsByTagName("item")].map((item) => ({
                    code: item.getElementsByTagName("code")[0]?.textContent || "",
                    name: item.getElementsByTagName("nameEn")[0]?.textContent || "",
                }));
                //console.log("Parsed currencies:", allCurrencies);
                setCurrencies(allCurrencies);
            });
    }, []);

    useEffect(() => {
        const loadHistory = async () => {
            if(!selectedCurrency){
                setHistory([]);
                return;
            }

            setLoading(true);

            try{
                const url =
                    `/api/rates/by-currency?currency=${selectedCurrency}` +
                    (fromDate ? `&from=${fromDate}` : "") +
                    (toDate ? `&to=${toDate}` : "");
                
                const res = await fetch(url);
                const xml = await res.text();
                console.log(xml);
                const parser = new DOMParser();
                const dom = parser.parseFromString(xml, "application/xml");

                const items = [...dom.getElementsByTagName("item")].map((node) => ({
                    date: node.getElementsByTagName("date")[0]?.textContent || "",
                    rate: parseFloat(node.getElementsByTagName("rate")[0]?.textContent || "0"),
                }));
                
                setHistory(items);
            } catch (err){
                console.error("Error loading history:", err);
                setHistory([]);
            }finally{
                setLoading(false);
            }
        };
        loadHistory();
    }, [selectedCurrency, fromDate, toDate]);


   return(
    <main className="p-8">
        <h1 className="text-3xl font-bold mb-6">Exchange Rate History</h1>

        <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-6 max-w-4xl">
            <div>
                <label className="font-semibold">Currency</label>
                <select
                    value={selectedCurrency}
                    onChange={e => setSelectedCurrency(e.target.value)}
                    className="border p-2 rounded w-full mt-1"
                >
                    <option value="">Select currency</option>
                    {currencies.map(c => (
                    <option key={c.code} value={c.code}>
                        {c.code} - {c.name}
                    </option>
                    ))}
                </select>
            </div>

            <div>
                <label className="font-semibold">From</label>
                <input
                    type="date"
                    className="border p-2 rounded w-full mt-1"
                    value={fromDate}
                    max={toDate && toDate < today ? toDate : today} 
                    onChange={e => {
                    const newFrom = e.target.value;
                    setFromDate(newFrom);

                    if (toDate && newFrom > toDate) {
                        setToDate(newFrom);
                    }
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
                    onChange={e => {
                    const newTo = e.target.value;
                    setToDate(newTo);

                    if (fromDate && newTo < fromDate) {
                        setFromDate(newTo);
                    }
                    }}
                />
            </div>
        </div>

        {loading && <p>Loading history...</p>}


        {!loading && history.length === 0 && selectedCurrency && (
            <p className="text-center text-gray-500 mt-4">No data found for selected currency and dates.</p>
        )}

        {history.length > 0 && (
            <div className="bg-white p-6 rounded-xl shadow max-w-4xl mx-auto">
            <h2 className="text-xl font-semibold mb-4">Data Table</h2>
            <table className="w-full border">
                <thead className="bg-gray-200">
                <tr>
                    <th className="p-2 border">Date</th>
                    <th className="p-2 border">Exchange Rate (1 EUR = ...)</th>
                </tr>
                </thead>
                <tbody>
                {history.map((h, i) => (
                    <tr key={i}>
                    <td className="border p-2">{h.date}</td>
                    <td className="border p-2">{h.rate}</td>
                    </tr>
                ))}
                </tbody>
            </table>
            </div>
        )}
    </main>
  );
}