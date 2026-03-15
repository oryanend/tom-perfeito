import { AppError } from '../app-error';

export class PermissionError extends AppError {
  constructor() {
    super('You do not have permission to access this resource');
  }
}
