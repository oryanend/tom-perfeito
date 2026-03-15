import { AppError } from '../app-error';

export class ApiError extends AppError {
  constructor(
    message: string,
    public status: number,
    originalError?: unknown
  ) {
    super(message, originalError);
  }
}
