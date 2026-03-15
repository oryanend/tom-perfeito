import { AppError } from '../app-error';

export class AuthError extends AppError {
  constructor() {
    super('Authentication required');
  }
}
