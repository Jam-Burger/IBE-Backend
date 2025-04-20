// Lambda function for Slack CloudWatch Notifications
const https = require('https');
const url = require('url');

exports.handler = async (event) => {
    console.log('Event:', JSON.stringify(event, null, 2));

    // Parse the SNS message
    const message = JSON.parse(event.Records[0].Sns.Message);

    // Format the message for Slack
    const slackMessage = formatSlackMessage(message);

    // Send to Slack
    return await sendToSlack(slackMessage);
};

function formatSlackMessage(message) {
    // Determine if this is an alarm or OK notification
    const isAlarm = message.NewStateValue === 'ALARM';
    const color = isAlarm ? '#FF0000' : '#36a64f';

    // Format the time
    const time = new Date(message.StateChangeTime).toLocaleString();

    // Create a readable message based on alarm type
    let readableMessage = `${message.AlarmName} state changed to ${message.NewStateValue}`;
    let detailsField = [];

    // Extract current value from NewStateReason if available
    let currentValue = 'unknown';
    if (message.NewStateReason) {
        const match = message.NewStateReason.match(/\[(.*?)\]/);
        if (match) {
            currentValue = match[1].split(' ')[0]; // Take first value if multiple exist
        }
    }

    // Handle different types of alarms
    if (message.Trigger) {
        // For metric math expressions
        if (message.Trigger.Metrics) {
            const mathMetric = message.Trigger.Metrics.find(m => m.Expression);
            if (mathMetric) {
                switch (mathMetric.Id) {
                    case 'serviceHealth':
                        const healthPercentage = parseFloat(currentValue).toFixed(1);
                        readableMessage = isAlarm ?
                            `🚨 Service Health Alert: Only ${healthPercentage}% healthy` :
                            `✅ Service Health Restored: 100% healthy`;
                        
                        detailsField.push({
                            title: "Impact",
                            value: isAlarm ? 
                                "Service capacity is reduced. Check ECS task logs and events." : 
                                "All tasks are running normally.",
                            short: false
                        });
                        break;
                    case 'errorRate':
                        const errorRate = parseFloat(currentValue).toFixed(1);
                        readableMessage = isAlarm ?
                            `🚨 High Error Rate: ${errorRate}% of requests are failing` :
                            `✅ Error Rate Normal: Below threshold`;
                        
                        detailsField.push({
                            title: "Impact",
                            value: isAlarm ?
                                "Service is returning elevated 4XX/5XX errors. Check application logs." :
                                "Error rates have returned to normal levels.",
                            short: false
                        });
                        break;
                }
            }
        }
        // For standard metrics
        else if (message.Trigger.MetricName) {
            const metricName = message.Trigger.MetricName;
            const threshold = message.Trigger.Threshold;
            const currentValueNum = parseFloat(currentValue);

            switch (metricName) {
                case 'CPUUtilization':
                    readableMessage = isAlarm ?
                        `🚨 High CPU Usage: ${currentValueNum.toFixed(1)}% utilization` :
                        `✅ CPU Usage Normal: Below ${threshold}%`;
                    break;
                case 'MemoryUtilization':
                    readableMessage = isAlarm ?
                        `🚨 High Memory Usage: ${currentValueNum.toFixed(1)}% utilization` :
                        `✅ Memory Usage Normal: Below ${threshold}%`;
                    break;
                case 'TargetResponseTime':
                    readableMessage = isAlarm ?
                        `🚨 High Latency: ${currentValueNum.toFixed(2)}s response time` :
                        `✅ Latency Normal: Below ${threshold}s`;
                    break;
            }

            if (isAlarm) {
                detailsField.push({
                    title: "Metric Details",
                    value: `Current: ${currentValueNum.toFixed(2)}, Threshold: ${threshold}`,
                    short: false
                });
            }
        }
    }

    // Add state change reason for context
    if (message.NewStateReason) {
        detailsField.push({
            title: "Additional Info",
            value: message.NewStateReason,
            short: false
        });
    }

    // Create the Slack message
    return {
        attachments: [
            {
                fallback: readableMessage,
                color: color,
                title: readableMessage,
                fields: [
                    ...detailsField,
                    {
                        title: "Time",
                        value: time,
                        short: true
                    },
                    {
                        title: "Region",
                        value: message.Region,
                        short: true
                    }
                ],
                footer: "AWS CloudWatch Alarm"
            }
        ]
    };
}

async function sendToSlack(message) {
    // Get the webhook URL from environment variables
    const webhookUrl = process.env.SLACK_WEBHOOK_URL;
    if (!webhookUrl) {
        throw new Error('Environment variable SLACK_WEBHOOK_URL not set');
    }

    // Parse the webhook URL
    const parsedUrl = url.parse(webhookUrl);

    // Prepare the request options
    const options = {
        hostname: parsedUrl.hostname,
        path: parsedUrl.path,
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        }
    };

    // Convert the message to JSON
    const postData = JSON.stringify(message);

    // Return a new Promise for the HTTPS request
    return new Promise((resolve, reject) => {
        const req = https.request(options, (res) => {
            let responseBody = '';

            res.on('data', (chunk) => {
                responseBody += chunk;
            });

            res.on('end', () => {
                if (res.statusCode < 200 || res.statusCode >= 300) {
                    return reject(new Error(`Status Code: ${res.statusCode}, Body: ${responseBody}`));
                }

                resolve({
                    statusCode: 200,
                    body: 'Message sent to Slack successfully'
                });
            });
        });

        req.on('error', (error) => {
            reject(error);
        });

        req.write(postData);
        req.end();
    });
}