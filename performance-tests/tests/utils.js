import http from 'k6/http';

/**
 * Converts an object of query parameters to a URL query string
 * @param {Object} params - The query parameters object
 * @returns {string} - The formatted query string
 */
export function buildQueryString(params) {
    if (!params) return '';
    
    const queryParts = [];
    for (const [key, value] of Object.entries(params)) {
        if (value !== null && value !== undefined) {
            queryParts.push(`${encodeURIComponent(key)}=${encodeURIComponent(value)}`);
        }
    }
    
    return queryParts.length > 0 ? `?${queryParts.join('&')}` : '';
}

/**
 * Builds a complete URL with query parameters
 * @param {string} baseUrl - The base URL
 * @param {string} path - The API path
 * @param {Object} queryParams - The query parameters object
 * @returns {string} - The complete URL with query parameters
 */
export function buildUrl(baseUrl, path, queryParams) {
    const queryString = buildQueryString(queryParams);
    return `${baseUrl}${path}${queryString}`;
}

/**
 * Makes an HTTP request based on the endpoint configuration
 * @param {string} baseUrl - The base URL for the API
 * @param {Object} endpoint - The endpoint configuration object
 * @returns {Object} - The HTTP response
 */
export function makeRequest(baseUrl, endpoint) {
    const url = buildUrl(baseUrl, endpoint.path, endpoint.queryParams);
    let response;

    switch (endpoint.method) {
        case 'GET':
            response = http.get(url);
            break;
        case 'POST':
            response = http.post(url, JSON.stringify(endpoint.body), {
                headers: { 'Content-Type': 'application/json' }
            });
            break;
        case 'PUT':
            response = http.put(url, JSON.stringify(endpoint.body), {
                headers: { 'Content-Type': 'application/json' }
            });
            break;
        case 'DELETE':
            response = http.del(url);
            break;
        default:
            throw new Error(`Unsupported HTTP method: ${endpoint.method}`);
    }

    return response;
} 