'use client';

import { useEffect, useState } from "react";
import { Currency } from "@/app/types";
import Header from "@/app/components/Header";
import CurrencySelect from "@/app/components/CurrencySelect";
import ResultCard from "@/app/components/ResultCard";

export default function CalculatorPage() {
    const [currencies, setCurrencies] = useState<Currency[]>([]);
    const [fromCurrency, setFromCurrency] = useState("");
    const [toCurrency, setToCurrency] = useState("");
    const [amount, setAmount] = useState("");
    const [convertedAmount, setConvertedAmount] = useState<number | null>(null);
    const [rateUsed, setRateUsed] = useState<number | null>(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState("");

    useEffect(() => {
        fetch("/api/currencies/with-rates")
        .then(res => res.text())
        .then(xml => {
            const parser = new DOMParser();
            const dom = parser.parseFromString(xml, "application/xml");
            const allCurrencies: Currency[] = [...dom.getElementsByTagName("item")].map(item => ({
            code: item.getElementsByTagName("code")[0]?.textContent || "",
            name: item.getElementsByTagName("nameEn")[0]?.textContent || "",
            }));
            setCurrencies(allCurrencies);
        });
    }, []);

    const handleConvert = async () => {
        if (!fromCurrency || !toCurrency || !amount) {
            setError("Please select currencies and enter amount.");
            return;
        }

        setError("");
        setLoading(true);
        setConvertedAmount(null);
        setRateUsed(null);

        try {
            
            const res = await fetch(
                `/api/rates/convert?from=${fromCurrency}&to=${toCurrency}&amount=${amount}`
            );

            const xmlText = await res.text();
            const parser = new DOMParser();
            const xml = parser.parseFromString(xmlText, "application/xml");

            const errorNode = xml.getElementsByTagName("Error")[0];
            if (errorNode) {
                const errorMessage =
                    errorNode.getElementsByTagName("message")[0]?.textContent || "Unknown error";
                setError(errorMessage);
                alert(errorMessage);
                throw new Error(errorMessage); 
            }

            const converted = xml.getElementsByTagName("convertedAmount")[0]?.textContent;
            const rate = xml.getElementsByTagName("rateUsed")[0]?.textContent;

            if (!converted || !rate) {
                throw new Error("Invalid response from server");
            }

            setConvertedAmount(parseFloat(converted));
            setRateUsed(parseFloat(rate));
        } catch (err) {
            console.error("Conversion error:", err);
            setError((err as Error).message || "Failed to convert currency.");
        } finally {
            setLoading(false);
        }
    };

    return (
        <main className="flex flex-1 flex-col">
        <Header title="Currency Calculator" showBack={true}/>

        <section className="p-8 max-w-3xl mx-auto">

            <div className="grid grid-cols-1 md:grid-cols-3 gap-4 mb-6">
                <CurrencySelect
                    label="From"
                    value={fromCurrency}
                    onChange={(v) => {
                    setFromCurrency(v);
                    setConvertedAmount(null);
                    setRateUsed(null);
                    }}
                    currencies={currencies}
                />

                <CurrencySelect
                    label="To"
                    value={toCurrency}
                    onChange={(v) => {
                        setToCurrency(v);
                        setConvertedAmount(null);
                        setRateUsed(null);
                    }}
                    currencies={currencies.filter((c) => c.code !== fromCurrency)}
                />

            <div>
                <label className="font-semibold">Amount</label>
                <input
                type="number"
                min="0"
                placeholder = "10.50"
                className="border p-2 rounded w-full mt-1"
                value={amount}
                onChange={(e) => {
                    setAmount(e.target.value);
                    setConvertedAmount(null);
                    setRateUsed(null);
                }}
                onKeyDown={(e) => {
                    if (e.key === "-") {
                    e.preventDefault();
                    }
                }}
                />
            </div>
            </div>

            <button
            onClick={handleConvert}
            className="bg-green-600 text-white px-6 py-2 rounded cursor-pointer"
            >
            {loading ? "Converting..." : "Convert"}
            </button>

            {error && <p className="text-red-500 mt-4">{error}</p>}

            {convertedAmount !== null && rateUsed !== null && !error && (
            <ResultCard
                amount={amount}
                from={fromCurrency}
                to={toCurrency}
                converted={convertedAmount}
                rate={rateUsed}
            />
            )}
        </section>
        </main>
    );
}
