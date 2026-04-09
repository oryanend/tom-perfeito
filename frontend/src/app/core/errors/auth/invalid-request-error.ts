import { AppError } from '../app-error';

export class InvalidRequestError extends AppError {
  constructor() {
    super('Invalid Request, check if the infos is correctly filled.');
  }
}
