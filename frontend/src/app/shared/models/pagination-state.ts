export interface PaginationState<T> {
  content: T[];
  currentPage: number;
  totalPages: number;
  pages: number[];
}
