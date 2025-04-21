import {check, sleep} from 'k6';
import {Rate} from 'k6/metrics';
import {TEST_ENDPOINTS} from './endpoints.js';
import {BASE_CONFIG, MAIN_CONFIG} from './config.js';
import {makeRequest} from './utils.js';

// Custom metrics
const errorRate = new Rate('errors');

export const options = {
    stages: MAIN_CONFIG.STAGES,
    thresholds: MAIN_CONFIG.THRESHOLDS,
    cloud: {
        projectID: 3762705,
        name: 'Main Tests'
    }
};

export default function () {
    TEST_ENDPOINTS.forEach(endpoint => {
        const response = makeRequest(BASE_CONFIG.BASE_URL, endpoint);

        check(response, {
            'status is 200': (r) => r.status === 200,
            'response time < 500ms': (r) => r.timings.duration < 500,
        });

        errorRate.add(response.status !== 200);
        sleep(MAIN_CONFIG.DELAY);
    });
}