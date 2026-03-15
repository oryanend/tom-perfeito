import { ErrorHandler, Injectable } from '@angular/core';
import { AppError } from '../errors/app-error';
import { AuthError } from '../errors/auth/auth-error';
import { PermissionError } from '../errors/auth/permission-error';
import { NetworkError } from '../errors/network/network-error';
import { ApiError } from '../errors/api/api-errors';

@Injectable()
export class GlobalErrorHandler implements ErrorHandler {
  handleError(error: unknown): void {
    if (error instanceof AppError) {
      this.handleAppError(error);
      return;
    }

    console.error('Unexpected error:', error);
  }

  private handleAppError(error: AppError) {
    const handlers = new Map<typeof AppError, (error: AppError) => void>([
      [AuthError, (e) => console.error('Auth Error:', e.message)],
      [PermissionError, (e) => console.error('Permission Error:', e.message)],
      [NetworkError, (e) => console.error('Network Error:', e.message)],
      [ApiError, (e) => console.error('API Error:', e.message)],
    ]);

    const handler = handlers.get(error.constructor as typeof AppError);

    if (handler) {
      handler(error);
      return;
    }

    console.error('Application Error:', error.message);
  }
}
