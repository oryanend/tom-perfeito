import { UserMin } from './user-min';

export interface Music {
  id: string;
  title: string;
  description: string;
  createdAt: string;
  createdBy: UserMin;
  link: string;
}
