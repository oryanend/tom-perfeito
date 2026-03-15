import { NetworkError } from './network-error';

describe('NetworkError', () => {
  it('should create an instance', () => {
    expect(new NetworkError()).toBeTruthy();
  });
});
