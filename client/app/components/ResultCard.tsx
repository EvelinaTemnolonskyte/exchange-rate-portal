interface ResultCardProps {
  amount: string;
  from: string;
  to: string;
  converted: number;
  rate: number;
}

export default function ResultCard({
  amount,
  from,
  to,
  converted,
  rate,
}: ResultCardProps) {
  return (
    <div className="mt-6 bg-white p-6 rounded-xl shadow text-center">
      <p className="text-xl font-semibold mb-2">
        {amount} {from} = {converted.toFixed(4)} {to}
      </p>
      <p className="text-gray-600">
        1 {from} = {rate.toFixed(4)} {to}
      </p>
    </div>
  );
}
