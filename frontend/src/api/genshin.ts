import type { DailyNote } from '../types/genshin';

const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;

export async function fetchDailyNote(): Promise<DailyNote> {
  const res = await fetch(`${API_BASE_URL}/api/genshin/daily-note`);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  return res.json() as Promise<DailyNote>;
}
