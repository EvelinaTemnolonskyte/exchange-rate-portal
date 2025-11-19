import { useRouter } from "next/navigation";

export default function Header({ title }: { title: string }) {
  const router = useRouter();

  return (
    <header className="flex flex-1 items-center gap-4 py-6 px-4 bg-[#45b400]">
      <button
        onClick={() => router.back()}
        className="bg-white text-[#45b400] px-3 py-1.5 rounded text-sm shrink-0 cursor-pointer" 
      >
        ← Back
      </button>

      <h1 className="text-2xl font-bold text-white min-w-0 px-2">
        {title}
      </h1>
    </header>
  );
}
