// Lambda function for Slack CloudWatch Notifications
const https = require('https');
const url = require('url');
const fs = require('fs');
const nodemailer = require('nodemailer');
const path = require('path');
const Handlebars = require('handlebars');

// Read email template
const templatePath = path.join(__dirname, 'email-template.html');
const template = Handlebars.compile(fs.readFileSync(templatePath, 'utf8'));

// Create email transporter
const transporter = nodemailer.createTransport({
    host: process.env.SMTP_HOST,
    port: process.env.SMTP_PORT,
    secure: process.env.SMTP_PORT === '465',
    auth: {
        user: process.env.MAIL_USERNAME,
        pass: process.env.MAIL_PASSWORD
    }
});

exports.handler = async (event) => {
    console.log('Event:', JSON.stringify(event, null, 2));

    // Parse the SNS message
    const message = JSON.parse(event.Records[0].Sns.Message);

    // Format the message for Slack
    const slackMessage = formatSlackMessage(message);

    // Send to Slack
    await sendToSlack(slackMessage);

    // Send email notification
    await sendEmailNotification(message);

    return {
        statusCode: 200,
        body: 'Notifications sent successfully'
    };
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

async function sendEmailNotification(message) {
    // Extract datapoints from the state change reason
    let datapoints = [];
    if (message.NewStateReason) {
        // Extract the datapoints section using a more precise regex
        const datapointsMatch = message.NewStateReason.match(/\[(.*?)\]/);
        if (datapointsMatch) {
            // Split the datapoints string and process each one
            const datapointsStr = datapointsMatch[1];
            const points = datapointsStr.split(', ');
            
            datapoints = points.map(point => {
                // Extract value and timestamp using a more precise regex
                const match = point.match(/(\d+\.\d+)\s+\((\d{2}\/\d{2}\/\d{2}\s+\d{2}:\d{2}:\d{2})\)/);
                if (match) {
                    const [_, value, timestamp] = match;
                    // Convert the timestamp to a more readable format
                    const [date, time] = timestamp.split(' ');
                    const [day, month, year] = date.split('/');
                    const formattedDate = `20${year}-${month}-${day}T${time}`;
                    
                    return {
                        value: parseFloat(value).toFixed(2),
                        timestamp: new Date(formattedDate).toLocaleString()
                    };
                }
                return null;
            }).filter(Boolean); // Remove any null entries
        }
    }

    // Prepare email data
    const emailData = {
        alarmName: message.AlarmName,
        newStateValue: message.NewStateValue,
        stateChangeTime: new Date(message.StateChangeTime).toLocaleString(),
        region: message.Region,
        trigger: message.Trigger,
        newStateReason: message.NewStateReason,
        datapoints: datapoints,
        stateColor: message.NewStateValue === 'ALARM' ? '#dc3545' : 
                   message.NewStateValue === 'OK' ? '#28a745' : '#ffc107'
    };

    // Generate HTML content
    const html = template(emailData);

    // Send email
    try {
        await transporter.sendMail({
            from: process.env.MAIL_FROM,
            to: process.env.MAIL_TO,
            subject: `CloudWatch Alarm: ${message.AlarmName} - ${message.NewStateValue}`,
            html: html
        });
        console.log('Email sent successfully');
        return {
            statusCode: 200,
            body: JSON.stringify({ message: 'Email sent successfully' })
        };
    } catch (error) {
        console.error('Error sending email:', error);
        throw error;
    }
}