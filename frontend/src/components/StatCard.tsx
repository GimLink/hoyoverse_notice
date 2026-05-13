import type { ReactNode } from 'react';

type Accent = 'amber' | 'sky' | 'emerald' | 'violet';

type StatCardProps = {
  title: string;
  current: number;
  max: number;
  icon?: string;
  accent?: Accent;
  children?: ReactNode;
};

const ACCENTS: Record<Accent, { bar: string; glow: string }> = {
  amber: {
    bar: 'bg-gradient-to-r from-amber-500 to-amber-300',
    glow: 'hover:shadow-amber-500/20',
  },
  sky: {
    bar: 'bg-gradient-to-r from-sky-500 to-sky-300',
    glow: 'hover:shadow-sky-500/20',
  },
  emerald: {
    bar: 'bg-gradient-to-r from-emerald-500 to-emerald-300',
    glow: 'hover:shadow-emerald-500/20',
  },
  violet: {
    bar: 'bg-gradient-to-r from-violet-500 to-violet-300',
    glow: 'hover:shadow-violet-500/20',
  },
};

export function StatCard({
  title,
  current,
  max,
  icon,
  accent = 'amber',
  children,
}: StatCardProps) {
  const ratio = max > 0 ? Math.min(current / max, 1) * 100 : 0;
  const isFull = current >= max && max > 0;
  const styles = ACCENTS[accent];

  return (
    <section
      className={`group relative overflow-hidden rounded-2xl p-5 bg-slate-900/60 backdrop-blur-sm ring-1 ring-slate-800 transition-all duration-300 hover:-translate-y-0.5 hover:ring-slate-700 hover:shadow-2xl ${styles.glow}`}
    >
      {icon && (
        <img
          src={icon}
          alt=""
          aria-hidden="true"
          className="absolute -right-4 -top-4 w-32 h-32 opacity-10 group-hover:opacity-20 transition-opacity object-contain pointer-events-none"
        />
      )}

      <div className="relative">
        <div className="flex items-center gap-2 mb-3">
          {icon && <img src={icon} alt="" className="w-7 h-7 object-contain" />}
          <h2 className="text-sm uppercase tracking-wider text-slate-400">{title}</h2>
        </div>

        <div className="flex items-baseline gap-2 mb-3">
          <span className="text-4xl font-semibold text-slate-50">{current}</span>
          <span className="text-base text-slate-500">/ {max}</span>
          {isFull && (
            <span className="ml-auto text-xs font-semibold text-emerald-400 tracking-wider">
              FULL
            </span>
          )}
        </div>

        <div className="h-1.5 w-full rounded-full bg-slate-800 overflow-hidden mb-3">
          <div
            className={`h-full rounded-full ${styles.bar} transition-all duration-500`}
            style={{ width: `${ratio}%` }}
          />
        </div>

        {children && <div className="text-sm text-slate-400">{children}</div>}
      </div>
    </section>
  );
}
