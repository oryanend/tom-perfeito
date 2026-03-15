import { AppError } from './app-error';

describe('AppError', () => {
  it('should create an instance', () => {
    expect(new AppError()).toBeTruthy();
  });
});
