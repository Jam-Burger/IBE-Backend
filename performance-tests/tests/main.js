import http from 'k6/http';
import {check, sleep} from 'k6';
import {Rate} from 'k6/metrics';
import {htmlReport} from 'https://raw.githubusercontent.com/benc-uk/k6-reporter/main/dist/bundle.js';
import {TEST_ENDPOINTS} from './endpoints.js';
import {BASE_CONFIG, MAIN_CONFIG, REPORT_CONFIG} from './config.js';

// Custom metrics
const errorRate = new Rate('errors');

// Detailed endpoint tracking
const endpointMetrics = {
    responseTimes: {}, successRates: {}, errorRates: {}, requestCounts: {}, paths: {},
};

function makeRequest(endpoint) {
    const url = `${BASE_CONFIG.BASE_URL}${endpoint.path}`;
    let response;

    switch (endpoint.method) {
        case 'GET':
            response = http.get(url, {
                params: endpoint.queryParams,
            });
            break;
        case 'POST':
            response = http.post(url, JSON.stringify(endpoint.body), {
                headers: {'Content-Type': 'application/json'}, params: endpoint.queryParams,
            });
            break;
        case 'PUT':
            response = http.put(url, JSON.stringify(endpoint.body), {
                headers: {'Content-Type': 'application/json'}, params: endpoint.queryParams,
            });
            break;
        case 'DELETE':
            response = http.del(url, null, {
                params: endpoint.queryParams,
            });
            break;
        default:
            throw new Error(`Unsupported HTTP method: ${endpoint.method}`);
    }

    return response;
}

function updateEndpointMetrics(endpoint, response) {
    if (!endpointMetrics.responseTimes[endpoint.path]) {
        endpointMetrics.responseTimes[endpoint.path] = [];
        endpointMetrics.successRates[endpoint.path] = 0;
        endpointMetrics.errorRates[endpoint.path] = 0;
        endpointMetrics.requestCounts[endpoint.path] = 0;
        endpointMetrics.paths[endpoint.path] = {
            url: `${BASE_CONFIG.BASE_URL}${endpoint.path}`,
            method: endpoint.method,
            body: endpoint.body,
            queryParams: endpoint.queryParams,
            totalRequests: 0,
            successfulRequests: 0,
            failedRequests: 0,
            minResponseTime: Infinity,
            maxResponseTime: 0,
            totalResponseTime: 0,
        };
    }

    const pathMetrics = endpointMetrics.paths[endpoint.path];
    const responseTime = response.timings.duration;

    pathMetrics.totalRequests++;
    pathMetrics.totalResponseTime += responseTime;
    pathMetrics.minResponseTime = Math.min(pathMetrics.minResponseTime, responseTime);
    pathMetrics.maxResponseTime = Math.max(pathMetrics.maxResponseTime, responseTime);

    if (response.status === 200) {
        pathMetrics.successfulRequests++;
        endpointMetrics.successRates[endpoint.path]++;
    } else {
        pathMetrics.failedRequests++;
        endpointMetrics.errorRates[endpoint.path]++;
    }

    endpointMetrics.responseTimes[endpoint.path].push(responseTime);
    endpointMetrics.requestCounts[endpoint.path]++;
}

// Main test function
export default function (data) {
    TEST_ENDPOINTS.MAIN.forEach(endpoint => {
        const response = makeRequest(endpoint);

        check(response, {
            'status is 200': (r) => r.status === 200, 'response time < 500ms': (r) => r.timings.duration < 500,
        });

        errorRate.add(response.status !== 200);
        updateEndpointMetrics(endpoint, response);
        sleep(MAIN_CONFIG.DELAY);
    });
}

export function handleSummary(data) {
    // Calculate and log endpoint summaries
    console.log('\nEndpoint Performance Summary:');
    console.log('============================');

    Object.keys(endpointMetrics.paths).forEach(path => {
        const pathMetrics = endpointMetrics.paths[path];
        const avgTime = pathMetrics.totalResponseTime / pathMetrics.totalRequests;
        const successRate = (pathMetrics.successfulRequests / pathMetrics.totalRequests) * 100;
        const errorRate = (pathMetrics.failedRequests / pathMetrics.totalRequests) * 100;

        console.log(`\nEndpoint: ${path}`);
        console.log(`Method: ${pathMetrics.method}`);
        console.log(`URL: ${pathMetrics.url}`);
        if (pathMetrics.body) {
            console.log(`Body: ${JSON.stringify(pathMetrics.body, null, 2)}`);
        }
        if (pathMetrics.queryParams) {
            console.log(`Query Params: ${JSON.stringify(pathMetrics.queryParams, null, 2)}`);
        }
        console.log(`Average Response Time: ${avgTime.toFixed(2)}ms`);
        console.log(`Min Response Time: ${pathMetrics.minResponseTime.toFixed(2)}ms`);
        console.log(`Max Response Time: ${pathMetrics.maxResponseTime.toFixed(2)}ms`);
        console.log(`Success Rate: ${successRate.toFixed(2)}%`);
        console.log(`Error Rate: ${errorRate.toFixed(2)}%`);
        console.log(`Total Requests: ${pathMetrics.totalRequests}`);
        console.log(`Successful Requests: ${pathMetrics.successfulRequests}`);
        console.log(`Failed Requests: ${pathMetrics.failedRequests}`);
    });

    const reportPath = `${REPORT_CONFIG.OUTPUT_DIR}/${REPORT_CONFIG.getFilename('main')}`;
    console.log(`\nGenerating HTML report: ${reportPath}`);

    return {
        [reportPath]: htmlReport(data),
    };
}