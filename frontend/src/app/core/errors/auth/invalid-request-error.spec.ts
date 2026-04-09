import { InvalidRequestError } from './invalid-request-error';

describe('InvalidRequestError', () => {
  it('should create an instance', () => {
    expect(new InvalidRequestError()).toBeTruthy();
  });
});
