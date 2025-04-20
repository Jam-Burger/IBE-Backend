  // generateTemplate.js

  exports.generateEmailHtml = (offer) => {
    return `
    <html>
      <head>
        <style>
          body {
            font-family: 'Segoe UI', sans-serif;
            background-color: #f4f6f9;
            margin: 0;
            padding: 0;
          }
          .container {
            max-width: 600px;
            margin: 40px auto;
            background-color: #ffffff;
            border-radius: 10px;
            box-shadow: 0 4px 12px rgba(0,0,0,0.1);
            overflow: hidden;
          }
          .header {
            background-color: #2d89ef;
            padding: 20px;
            text-align: center;
            color: white;
          }
          .header h1 {
            margin: 0;
            font-size: 26px;
          }
          .content {
            padding: 30px;
          }
          .content p {
            font-size: 16px;
            line-height: 1.6;
            color: #333;
          }
          .promo-code {
            margin: 30px 0;
            text-align: center;
            font-size: 24px;
            font-weight: bold;
            background-color: #e6f0ff;
            padding: 15px;
            border-radius: 8px;
            color: #2d89ef;
            border: 2px dashed #2d89ef;
          }
          .footer {
            text-align: center;
            padding: 20px;
            font-size: 13px;
            color: #888;
          }
          .highlight {
            color: #2d89ef;
            font-weight: bold;
          }
        </style>
      </head>
      <body>
        <div class="container">
          <div class="header">
            <h1>${offer.title}</h1>
          </div>
          <div class="content">
            <p>${offer.description}</p>
            <p><span class="highlight">Valid:</span> ${offer.start_date} to ${offer.end_date}</p>
            ${offer.discount_percentage ? `<p><span class="highlight">Discount:</span> ${offer.discount_percentage}% off</p>` : ''}
            ${offer.promo_code ? `<div class="promo-code">Use Code: ${offer.promo_code}</div>` : ''}
            <p>We can’t wait to see you enjoy this special time with us!</p>
          </div>
          <div class="footer">
            &copy; ${new Date().getFullYear()} Hufflepuff Hospitality. All rights reserved.
          </div>
        </div>
      </body>
    </html>
    `;
  };
  