import type { ReactNode } from 'react';

type StatCardProps = {
  title: string;
  current: number;
  max: number;
  children?: ReactNode;
};

export function StatCard({ title, current, max, children }: StatCardProps) {
  return (
    <section className="bg-slate-900 border border-slate-800 rounded-lg p-4">
      <h2 className="text-sm uppercase tracking-wider text-slate-400 mb-2">{title}</h2>
      <p className="text-3xl font-semibold text-slate-50">
        {current}
        <span className="text-base text-slate-500"> / {max}</span>
      </p>
      {children && <div className="mt-2 text-sm text-slate-400">{children}</div>}
    </section>
  );
}
