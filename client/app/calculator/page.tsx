'use client';

import { useEffect, useState } from "react";
import { Currency, CurrencyConversion } from "@/app/types";
import Header from "@/app/components/Header";
import CurrencySelect from "@/app/components/CurrencySelect";
import ResultCard from "@/app/components/ResultCard";
import { ensureResponseOk, getRequestErrorMessage } from "@/app/apiErrors";

export default function CalculatorPage() {
    const [currencies, setCurrencies] = useState<Currency[]>([]);
    const [fromCurrency, setFromCurrency] = useState("");
    const [toCurrency, setToCurrency] = useState("");
    const [amount, setAmount] = useState("");
    const [convertedAmount, setConvertedAmount] = useState<number | null>(null);
    const [rateUsed, setRateUsed] = useState<number | null>(null);
    const [loading, setLoading] = useState(false);
    const [currenciesError, setCurrenciesError] = useState("");
    const [error, setError] = useState("");
    const toCurrencyMinorUnitDigits =
        currencies.find((currency) => currency.code === toCurrency)?.minorUnitDigits ?? 2;

    useEffect(() => {
        const fetchCurrencies = async () => {
            try {
                const res = await fetch("/api/currencies?withRates=true");
                await ensureResponseOk(res, "Unable to load currencies. Please try again.");
                setCurrencies(await res.json() as Currency[]);
            } catch (err) {
                console.error("Currency loading error:", err);
                setCurrenciesError(getRequestErrorMessage(err, "Unable to load currencies. Please try again."));
            }
        };

        fetchCurrencies();
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
            const params = new URLSearchParams({ fromCurrency, toCurrency, amount });
            const res = await fetch(`/api/conversions?${params}`);
            await ensureResponseOk(res, "Couldn't convert that amount. Check your selections and try again.");

            const conversion = await res.json() as CurrencyConversion;
            setConvertedAmount(conversion.convertedAmount);
            setRateUsed(conversion.rateUsed);
        } catch (err) {
            console.error("Conversion error:", err);
            setError(getRequestErrorMessage(err, "Couldn't convert that amount. Please try again."));
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
                {currenciesError && <p role="alert" className="text-red-600 mt-4">{currenciesError}</p>}

                {convertedAmount !== null && rateUsed !== null && !error && (
                    <ResultCard
                        amount={amount}
                        from={fromCurrency}
                        to={toCurrency}
                        converted={convertedAmount}
                        rate={rateUsed}
                        minorUnitDigits={toCurrencyMinorUnitDigits}
                    />
                )}
            </section>
        </main>
    );
}
