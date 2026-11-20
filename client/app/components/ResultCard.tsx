interface ResultCardProps {
  amount: string;
  from: string;
  to: string;
  converted: number;
  rate: number;
  minorUnitDigits: number;
}

export default function ResultCard({
  amount,
  from,
  to,
  converted,
  rate,
  minorUnitDigits,
}: ResultCardProps) {
  const formattedConverted = new Intl.NumberFormat(undefined, {
    minimumFractionDigits: minorUnitDigits,
    maximumFractionDigits: minorUnitDigits,
  }).format(converted);

  return (
    <div className="mt-6 bg-white p-6 rounded-xl shadow text-center">
      <p className="text-xl font-semibold mb-2">
        {amount} {from} = {formattedConverted} {to}
      </p>
      <p className="text-gray-600">
        1 {from} = {rate.toFixed(7)} {to}
      </p>
    </div>
  );
}
