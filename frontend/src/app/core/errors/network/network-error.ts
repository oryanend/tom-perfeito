import { AppError } from '../app-error';

export class NetworkError extends AppError {
  constructor(message = 'Backend is offline right now, please try again later') {
    super(message);
  }
}
