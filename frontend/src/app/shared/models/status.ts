export interface Status {
  updated_at: string;
  dependencies: {
    database: {
      status: string;
      version: string;
      max_connections: number;
      open_connections: number;
      latency: {
        first_query: number;
        second_query: number;
        third_query: number;
      };
    };
    webserver: {
      status: string;
      version: string;
      provider: string;
      environment: string;
    };
  };
}
