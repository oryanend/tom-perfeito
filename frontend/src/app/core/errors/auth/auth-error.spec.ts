import { AuthError } from './auth-error';

describe('AuthError', () => {
  it('should create an instance', () => {
    expect(new AuthError()).toBeTruthy();
  });
});
