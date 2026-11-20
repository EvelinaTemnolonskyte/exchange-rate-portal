import type { Currency } from "@/app/types";
import Select, { type StylesConfig } from "react-select";

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
  const inputId = `currency-select-${label.toLowerCase()}`;
  const styles: StylesConfig<Currency, false> = {
    control: (base, state) => ({
      ...base,
      minHeight: 42,
      borderColor: state.isFocused ? "#45b400" : "#d1d5db",
      borderRadius: 6,
      boxShadow: state.isFocused ? "0 0 0 1px #45b400" : "none",
      "&:hover": {
        borderColor: state.isFocused ? "#45b400" : "#9ca3af",
      },
    }),
    menu: (base) => ({ ...base, zIndex: 20 }),
    option: (base, state) => ({
      ...base,
      backgroundColor: state.isSelected ? "#45b400" : state.isFocused ? "#f0fdf4" : "white",
      color: state.isSelected ? "white" : "#171717",
    }),
  };

  return (
    <div className="w-full">
      <label htmlFor={inputId} className="font-semibold">{label}</label>
      <Select<Currency, false>
        inputId={inputId}
        instanceId={inputId}
        aria-label={label}
        value={currencies.find((currency) => currency.code === value) ?? null}
        options={currencies}
        onChange={(currency) => onChange(currency?.code ?? "")}
        getOptionLabel={(currency) => `${currency.code} - ${currency.englishName}`}
        getOptionValue={(currency) => currency.code}
        placeholder="Search currencies..."
        noOptionsMessage={() => "No currencies found"}
        isClearable
        isSearchable
        styles={styles}
        className="mt-1"
      />
    </div>
  );
}

