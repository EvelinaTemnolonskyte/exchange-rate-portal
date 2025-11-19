import Link from "next/link";

interface PageCardProps {
  title: string;
  desc: string;
  href: string;
}

export default function PageCard({ title, desc, href }: PageCardProps) {
  return (
    <Link
      href={href}
      className="flex flex-col flex-1
                p-8 bg-white rounded-xl 
                border text-center block"
    >
      <h2 className="text-2xl font-semibold mb-2 text-gray-800">{title}</h2>
      <p className="text-gray-600">{desc}</p>
    </Link>
  );
}