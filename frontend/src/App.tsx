import { useQuery } from '@tanstack/react-query';
import { fetchDailyNote } from './api/genshin';
import { StatCard } from './components/StatCard';

function formatRemainingTime(seconds: string): string {
  const n = Number(seconds);
  if (n === 0) return 'Full';
  const h = Math.floor(n / 3600);
  const m = Math.floor((n % 3600) / 60);
  return h > 0 ? `${h}h ${m}m` : `${m}m`;
}

function App() {
  const { data, error, isLoading, isFetching, refetch } = useQuery({
    queryKey: ['genshin', 'daily-note'],
    queryFn: fetchDailyNote,
    refetchInterval: 60_000,
  });

  if (isLoading) {
    return <main className="min-h-screen bg-slate-950 text-slate-200 p-6">Loading…</main>;
  }
  if (error && !data) {
    return (
      <main className="min-h-screen bg-slate-950 text-rose-400 p-6">
        Error: {error.message}
      </main>
    );
  }
  if (!data) return null;

  return (
    <main className="min-h-screen bg-slate-950 text-slate-200 p-6">
      <div className="mx-auto max-w-4xl">
        <div className="flex items-center justify-between mb-6">
          <h1 className="text-2xl font-semibold text-slate-100">Genshin Daily Note</h1>
          <button
            type="button"
            onClick={() => refetch()}
            disabled={isFetching}
            className="px-3 py-1.5 text-sm bg-slate-800 hover:bg-slate-700 disabled:opacity-50 rounded-md text-slate-200"
          >
            {isFetching ? 'Loading…' : 'Refresh'}
          </button>
        </div>

        <div className="grid grid-cols-1 md:grid-cols-2 gap-4">
          <StatCard title="Resin" current={data.current_resin} max={data.max_resin}>
            Recovery: {formatRemainingTime(data.resin_recovery_time)}
          </StatCard>

          <StatCard title="Daily Tasks" current={data.finished_task_num} max={data.total_task_num}>
            Bonus: {data.is_extra_task_reward_received ? 'Claimed' : 'Not claimed'}
          </StatCard>

          <StatCard
            title="Expeditions"
            current={data.current_expedition_num}
            max={data.max_expedition_num}
          >
            <ul className="space-y-1">
              {data.expeditions.map((e, i) => (
                <li key={i} className="flex justify-between">
                  <span
                    className={e.status === 'Finished' ? 'text-emerald-400' : 'text-slate-400'}
                  >
                    {e.status}
                  </span>
                  <span className="text-slate-500">{formatRemainingTime(e.remained_time)}</span>
                </li>
              ))}
            </ul>
          </StatCard>

          <StatCard
            title="Realm Currency"
            current={data.current_home_coin}
            max={data.max_home_coin}
          >
            Recovery: {formatRemainingTime(data.home_coin_recovery_time)}
          </StatCard>
        </div>
      </div>
    </main>
  );
}

export default App;
