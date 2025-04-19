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

    // Add metric-specific details
    if (message.Trigger) {
        const metricName = message.Trigger.MetricName;
        const namespace = message.Trigger.Namespace;
        const threshold = message.Trigger.Threshold;
        const operator = message.Trigger.ComparisonOperator;

        let operatorText = '';
        if (operator.includes('GreaterThan')) {
            operatorText = 'exceeded';
        } else if (operator.includes('LessThan')) {
            operatorText = 'dropped below';
        }

        let value = 'unknown';
        if (message.NewStateReason && message.NewStateReason.includes('[')) {
            const match = message.NewStateReason.match(/\[(.*?)\]/);
            if (match) value = match[1];
        }

        detailsField = [
            {
                title: "Alarm Details",
                value: `${namespace}: ${metricName} ${operatorText} threshold of ${threshold} (current value: ${value})`,
                short: false
            }
        ];

        // Add more specific information based on metric type
        if (metricName === 'CPUUtilization') {
            readableMessage = isAlarm ?
                `🚨 HIGH CPU USAGE ALERT: ECS Service is experiencing high CPU utilization` :
                `✅ CPU usage has returned to normal levels`;
        } else if (metricName === 'MemoryUtilization') {
            readableMessage = isAlarm ?
                `🚨 HIGH MEMORY USAGE ALERT: ECS Service is experiencing high memory utilization` :
                `✅ Memory usage has returned to normal levels`;
        } else if (metricName === 'TargetResponseTime') {
            readableMessage = isAlarm ?
                `🚨 SLOW RESPONSE TIME ALERT: Service response time is too high` :
                `✅ Service response time has returned to acceptable levels`;
        } else if (metricName === 'HTTPCode_Target_5XX_Count') {
            readableMessage = isAlarm ?
                `🚨 SERVER ERROR ALERT: Service is returning 5XX errors` :
                `✅ Service is no longer returning 5XX errors`;
        }
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
                    },
                    {
                        title: "Alarm Description",
                        value: message.AlarmDescription || "No description provided",
                        short: false
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