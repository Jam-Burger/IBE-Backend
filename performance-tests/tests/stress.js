import { check, sleep } from 'k6';
import { Rate } from 'k6/metrics';
import { htmlReport } from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import { TEST_ENDPOINTS } from './endpoints.js';
import { BASE_CONFIG, STRESS_CONFIG, REPORT_CONFIG } from './config.js';
import { makeRequest } from './utils.js';

const errorRate = new Rate('errors');

export const options = {
    stages: STRESS_CONFIG.STAGES,
    thresholds: STRESS_CONFIG.THRESHOLDS,
    cloud: {
        projectID: 3762705,
        name: 'Stress Tests'
    }
};

export default function() {
    TEST_ENDPOINTS.forEach(endpoint => {
        const response = makeRequest(BASE_CONFIG.BASE_URL, endpoint);
        
        check(response, {
            'status is 200': (r) => r.status === 200,
            'response time < 1000ms': (r) => r.timings.duration < 1000,
        });

        errorRate.add(response.status !== 200);
        sleep(STRESS_CONFIG.DELAY);
    });
}

export function handleSummary(data) {
    const reportPath = `${REPORT_CONFIG.OUTPUT_DIR}/${REPORT_CONFIG.getFilename('stress')}`;
    return {
        [reportPath]: htmlReport(data),
    };
} 