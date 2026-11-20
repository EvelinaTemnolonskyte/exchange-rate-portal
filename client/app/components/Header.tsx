"use client";

import { useRouter } from "next/navigation";
import { ArrowLeft } from "lucide-react";

interface HeaderProps {
  title: string;
  showBack?: boolean; 
}

export default function Header({ title, showBack = false }: HeaderProps) {
  const router = useRouter();

  return (
    <header className="flex items-center gap-4 py-6 px-4 bg-[#45b400]">
      {showBack && (
        <button
          type="button"
          onClick={() => router.back()}
          className="inline-flex items-center gap-2 bg-white text-[#45b400] px-3 py-1.5 rounded text-sm shrink-0 cursor-pointer"
        >
          <ArrowLeft size={16} aria-hidden="true" />
          Back
        </button>
      )}

      <h1 className="text-2xl font-bold text-white min-w-0 px-2">
        {title}
      </h1>
    </header>
  );
}
