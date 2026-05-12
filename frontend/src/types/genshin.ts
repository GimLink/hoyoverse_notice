export type Expedition = {
  avatar_side_icon: string;
  status: 'Ongoing' | 'Finished';
  remained_time: string;
};

export type DailyNote = {
  current_resin: number;
  max_resin: number;
  resin_recovery_time: string;
  finished_task_num: number;
  total_task_num: number;
  is_extra_task_reward_received: boolean;
  current_expedition_num: number;
  max_expedition_num: number;
  expeditions: Expedition[];
  current_home_coin: number;
  max_home_coin: number;
  home_coin_recovery_time: string;
};
