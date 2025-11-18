import Link from "next/link";

export default function Home() {
  return (
    <main className="min-h-screen bg-gray-100">
      <section className="flex flex-col items-center justify-center text-center py-28 bg-[#45b400] shadow-md">
        <h1 className="text-5xl font-bold text-white mb-4">
          Exchange Rate Portal
        </h1>
      </section>

      <section className="max-w-4xl mx-auto mt-20 grid grid-cols-1 md:grid-cols-3 gap-6 px-6">

        <Link
          href="/rates"
          className="p-8 bg-white rounded-xl shadow hover:shadow-lg 
                     transition border text-center block"
        >
          <h2 className="text-2xl font-semibold mb-2 text-gray-800">
            Exchange Rates
          </h2>
          <p className="text-gray-600">
            View latest exchange rates from the Central Bank.
          </p>
        </Link>

        <Link
          href="/history"
          className="p-8 bg-white rounded-xl shadow hover:shadow-lg 
                     transition border text-center block"
        >
          <h2 className="text-2xl font-semibold mb-2 text-gray-800">
            Rate History
          </h2>
          <p className="text-gray-600">
            Explore historical exchange rate charts.
          </p>
        </Link>

        <Link
          href="/calculator"
          className="p-8 bg-white rounded-xl shadow hover:shadow-lg 
                     transition border text-center block"
        >
          <h2 className="text-2xl font-semibold mb-2 text-gray-800">
            Currency Calculator
          </h2>
          <p className="text-gray-600">
            Convert between currencies quickly.
          </p>
        </Link>

      </section>
    </main>
  );
}

