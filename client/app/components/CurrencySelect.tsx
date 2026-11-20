import { Currency } from "@/app/types";

interface CurrencySelectProps {
  label: string;
  value: string;
  onChange: (v: string) => void;
  currencies: Currency[];
}

export default function CurrencySelect({
  label,
  value,
  onChange,
  currencies,
}: CurrencySelectProps) {
  return (
    <div className="w-full">
      <label className="font-semibold">{label}</label>
      <select
        value={value}
        onChange={(e) => onChange(e.target.value)}
        className="border p-2 rounded w-full mt-1"
      >
        <option value="">Select currency</option>
        {currencies.map((c) => (
          <option key={c.code} value={c.code}>
            {c.code} - {c.englishName}
          </option>
        ))}
      </select>
    </div>
  );
}

