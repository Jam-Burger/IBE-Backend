export function generateEmailTemplate(data) {
    return `
    <html>
    <head>
        <style>
            body {
                font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                color: #333;
                margin: 0;
                padding: 20px;
                background-color: #f7f9fc;
            }
            .email-container {
                background-color: #fff;
                padding: 35px;
                border-radius: 12px;
                box-shadow: 0 4px 15px rgba(0, 0, 0, 0.08);
                max-width: 620px;
                margin: 0 auto;
            }
            .header {
                border-bottom: 2px solid #eaedf2;
                padding-bottom: 20px;
                margin-bottom: 25px;
            }
            h2 {
                color: #e63946;
                margin-bottom: 15px;
                font-size: 26px;
                letter-spacing: -0.5px;
            }
            h3 {
                margin-top: 28px;
                margin-bottom: 18px;
                color: #2b6cb0;
                font-size: 19px;
                border-left: 4px solid #2b6cb0;
                padding-left: 12px;
                letter-spacing: -0.3px;
            }
            .info {
                margin-bottom: 15px;
                display: flex;
                align-items: baseline;
            }
            .info strong {
                display: inline-block;
                width: 140px;
                color: #4a5568;
                font-weight: 600;
            }
            .info-value {
                font-weight: 500;
                color: #2d3748;
            }
            .time-window {
                background: linear-gradient(to right, #e6f2ff, #f0f7ff);
                border-radius: 10px;
                padding: 18px;
                margin: 20px 0;
                text-align: center;
                box-shadow: 0 2px 8px rgba(41, 128, 185, 0.1);
                border: 1px solid #d0e1f9;
            }
            .window-label {
                font-size: 15px;
                color: #3182ce;
                margin-bottom: 8px;
                font-weight: 500;
            }
            .window-time {
                font-weight: 600;
                color: #2b6cb0;
                font-size: 18px;
                padding: 5px 0;
            }
            /* New table styles */
            .staff-table {
                width: 100%;
                border-collapse: collapse;
                margin: 18px 0;
                background-color: #f8fafc;
                border-radius: 10px;
                overflow: hidden;
                box-shadow: 0 2px 6px rgba(0, 0, 0, 0.04);
            }
            .staff-table th, 
            .staff-table td {
                padding: 14px 20px;
                text-align: left;
                border-bottom: 1px solid #e2e8f0;
            }
            .staff-table th {
                background-color: #f0f7ff;
                color: #4a5568;
                font-weight: 600;
                font-size: 15px;
            }
            .staff-count {
                text-align: center;
                font-weight: 600;
                color: #2d3748;
                background-color: #ebf4ff;
                padding: 6px 12px;
                border-radius: 20px;
                min-width: 30px;
                display: inline-block;
                width: 30px;
            }
            .staff-total-row td {
                padding-top: 18px;
                padding-bottom: 18px;
                border-top: 2px solid #e2e8f0;
                border-bottom: none;
                font-weight: bold;
                color: #e53e3e;
            }
            .staff-total-count {
                text-align: center;
                font-weight: 700;
                color: #e53e3e;
                background-color: #fff5f5;
                padding: 6px 14px;
                border-radius: 20px;
                min-width: 30px;
                display: inline-block;
                width: 30px;
                border: 1px solid #fed7d7;
                font-size: 18px;
            }
            .alert-box {
                background-color: #fff5f5;
                border-left: 4px solid #e53e3e;
                padding: 18px;
                margin-top: 28px;
                border-radius: 8px;
                box-shadow: 0 2px 6px rgba(229, 62, 62, 0.1);
            }
            .alert {
                font-weight: bold;
                color: #e53e3e;
                margin: 0;
                line-height: 1.5;
            }
            .footer {
                margin-top: 35px;
                padding-top: 18px;
                font-size: 13px;
                color: #a0aec0; 
                text-align: center;
                border-top: 1px solid #edf2f7;
            }
        </style>
    </head>
    <body>
        <div class="email-container">
            <div class="header">
                <h2>🚨 Staff Shortage Alert</h2>
                <div class="info"><strong>Property:</strong> <span class="info-value">${data.propertyName}</span></div>
                <div class="info"><strong>Date:</strong> <span class="info-value">${data.date?.split("T")[0]}</span></div>
            </div>

            <div class="time-window">
                <div class="window-label">Critical Cleaning Window</div>
                <div class="window-time">
                    ${data.checkOutTime} (Checkout) → ${data.checkInTime} (Checkin)
                </div>
            </div>

            <h3>🧑‍🤝‍🧑 Additional Staff Required</h3>
            
            <!-- Replaced staff box with a proper table -->
            <table class="staff-table">
                <thead>
                    <tr>
                        <th>Staff Category</th>
                        <th style="text-align: center;">Required</th>
                    </tr>
                </thead>
                <tbody>
                    <tr>
                        <td>Critical Window (${data.checkOutTime} - ${data.checkInTime})</td>
                        <td style="text-align: center;"><span class="staff-count">${data.criticalWindowStaffNeeded || 0}</span></td>
                    </tr>
                    <tr>
                        <td>Before Check-in (${data.checkInTime})</td>
                        <td style="text-align: center;"><span class="staff-count">${data.additionalBeforeCheckInStaff || 0}</span></td>
                    </tr>
                    <tr>
                        <td>General</td>
                        <td style="text-align: center;"><span class="staff-count">${data.additionalGeneralStaff || 0}</span></td>
                    </tr>
                    <tr class="staff-total-row">
                        <td>Total Additional Staff</td>
                        <td style="text-align: center;"><span class="staff-total-count">${data.additionalStaffNeeded || 0}</span></td>
                    </tr>
                </tbody>
            </table>
            
            <div class="alert-box">
                <p class="alert">⚠️ Please arrange additional staff before <strong>${data.checkInTime}</strong> today to avoid service delays.</p>
            </div>
            
            <div class="footer">
                This is an automated alert from the Cleaning Scheduler System
            </div>
        </div>
    </body>
    </html>
    `;
}