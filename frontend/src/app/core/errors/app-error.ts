export class AppError extends Error {
  constructor(
    public override message: string,
    public originalError?: unknown
  ) {
    super(message);
    this.name = this.constructor.name;
  }
}
