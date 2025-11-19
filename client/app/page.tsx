import PageCard from "@/app/components/PageCard";
import Header from "./components/Header";

export default function Home() {
  return (
    <main className="flex flex-1 flex-col min-h-screen">
      <section className="flex flex-col items-center justify-center text-center px-2 py-28 bg-[#45b400] shadow-md">
        <h1 className="text-5xl font-bold text-white mb-4">
          Exchange Rate Portal
        </h1>
      </section>

      <section className="max-w-4xl mx-auto mt-20 grid grid-cols-1 md:grid-cols-2 gap-6 px-6 my-2">
        
        <PageCard
          title="Rate History"
          desc="Explore historical exchange rate charts."
          href="/history"
        />

        <PageCard
          title="Currency Calculator"
          desc="Convert between currencies quickly."
          href="/calculator"
        />

      </section>
    </main>
  );
}

