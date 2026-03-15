import { PermissionError } from './permission-error';

describe('PermissionError', () => {
  it('should create an instance', () => {
    expect(new PermissionError()).toBeTruthy();
  });
});
