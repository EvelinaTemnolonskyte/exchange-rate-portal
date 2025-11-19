'use client';

import { useEffect, useState } from "react";
import { LatestRateItem } from "@/app/types";

export default function Rates() {
  const [rates, setRates] = useState<LatestRateItem[]>([]);
  const [error, setError] = useState("");

  useEffect(() => {
    const loadRates = async () => {
      try {
        const res = await fetch("/api/ratest/latest");

        if (!res.ok) {
          throw new Error("Failed to load latest rates");
        }

        const xml = await res.text();
        const parser = new DOMParser();
        const dom = parser.parseFromString(xml, "application/xml");

        const items: LatestRateItem[] = [...dom.getElementsByTagName("item")].map(
          (node) => ({
            currency: node.getElementsByTagName("currency")[0]?.textContent || "",
            rate: parseFloat(node.getElementsByTagName("rate")[0]?.textContent || "0"),
          })
        );

        setRates(items);
      } catch (err: any) {
        console.error("Error loading latest rates:", err);
        setError("Could not load latest exchange rates.");
      }
    };

    loadRates();
  }, []);
}