// Base configuration
export const BASE_CONFIG = {
  BASE_URL: __ENV.BASE_URL || 'http://localhost:8080',
  DEFAULT_DELAY: 1, // seconds
};

// Smoke test configuration
export const SMOKE_CONFIG = {
  VUS: 1,
  DURATION: '1m',
  THRESHOLDS: {
    'http_req_duration': ['p(95)<200'],
    'http_req_failed': ['rate<0.1'], // Increased threshold to 10% for testing
  },
};

// Main test configuration
// export const MAIN_CONFIG = {
//   STAGES: [
//     { duration: '1m', target: 10 },
//     { duration: '3m', target: 10 },
//     { duration: '1m', target: 0 },
//   ],
//   THRESHOLDS: {
//     'http_req_duration': ['p(95)<500'],
//     'http_req_failed': ['rate<0.1'],
//   },
//   DELAY: 1, // seconds
// };

export const MAIN_CONFIG = {
    STAGES: [
      { duration: '1s', target: 10 },
      { duration: '2s', target: 10 },
      { duration: '1s', target: 0 },
    ],
    THRESHOLDS: {
      'http_req_duration': ['p(95)<500'],
      'http_req_failed': ['rate<0.1'],
    },
    DELAY: 1, // seconds
  };

// Stress test configuration
export const STRESS_CONFIG = {
  STAGES: [
    { duration: '2m', target: 50 },
    { duration: '5m', target: 50 },
    { duration: '2m', target: 100 },
    { duration: '5m', target: 100 },
    { duration: '2m', target: 0 },
  ],
  THRESHOLDS: {
    'http_req_duration': ['p(95)<1000'],
    'http_req_failed': ['rate<0.1'],
  },
  DELAY: 0.5, // seconds
};

// HTML Report configuration
export const REPORT_CONFIG = {
  OUTPUT_DIR: 'reports',
  getFilename: (testType) => {
    return `${testType}-report-[${BASE_CONFIG.BASE_URL.split("//")[1]}].html`;
  }
}; 