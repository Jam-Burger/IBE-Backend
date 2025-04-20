const { Pool } = require('pg');
const nodemailer = require('nodemailer');
const { S3 } = require('@aws-sdk/client-s3');
const { generateEmailHtml } = require('./generateTemplate');

const s3 = new S3();

// Initialize PostgreSQL pool
const pgPool = new Pool({
  host: process.env.DB_HOST,
  port: process.env.DB_PORT,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
  ssl: { rejectUnauthorized: false }
});

const transporter = nodemailer.createTransport({
  host: "smtp.gmail.com",
  port: 587,
  secure: false,
  auth: {
    user: process.env.MAIL_USERNAME,
    pass: process.env.MAIL_PASSWORD,
  },
});

exports.handler = async (event) => {
  const record = event.Records[0];
  const bucket = record.s3.bucket.name;
  const key = decodeURIComponent(record.s3.object.key.replace(/\+/g, ' '));

  let client;

  try {
    // Get the offer details from S3
    const s3Object = await s3.getObject({ Bucket: bucket, Key: key });
    // Handle S3 object body for SDK v3
    const bodyContents = await streamToString(s3Object.Body);
    const offerJson = JSON.parse(bodyContents);
    console.log('📦 Offer details:', offerJson);

    // Get database connection
    client = await pgPool.connect();

    // Insert offer into DB
    const insertQuery = `
      INSERT INTO special_offer (property_id, start_date, end_date, discount_percentage, title, description, promo_code)
      VALUES ($1, $2, $3, $4, $5, $6, $7)
      RETURNING id
    `;
    const insertValues = [
      offerJson.property_id,
      offerJson.start_date,
      offerJson.end_date,
      offerJson.discount_percentage,
      offerJson.title,
      offerJson.description,
      offerJson.promo_code
    ];
    const insertRes = await client.query(insertQuery, insertValues);
    console.log(`🎯 Offer inserted with ID: ${insertRes.rows[0].id}`);

    // Query emails from DB
    const res = await client.query(`
      SELECT billing_email 
      FROM guest_extension 
      WHERE special_offers_consent = true AND billing_email IS NOT NULL
    `);
    
    // Test email configuration
    try {
        await transporter.verify();
        console.log('✅ SMTP connection verified successfully');
    } catch (verifyErr) {
        console.error('❌ SMTP connection verification failed:', {
            error: verifyErr.message,
            code: verifyErr.code,
            host: process.env.MAIL_USERNAME ? process.env.MAIL_USERNAME.split('@')[1] : 'unknown'
        });
        throw verifyErr;
    }
    
    // Generate email HTML
    const emailHtml = generateEmailHtml(offerJson);

    console.log(`📧 Preparing to send emails to ${res.rows.length} recipients`);

    for (let row of res.rows) {
      try {
        const info = await transporter.sendMail({
          from: process.env.MAIL_USERNAME,
          to: row.billing_email,
          subject: `🎁 ${offerJson.title} - Limited Time Offer!`,
          html: emailHtml,
        });
        console.log(`✅ Email sent to ${row.billing_email}`, {
          messageId: info.messageId,
          response: info.response
        });
      } catch (emailErr) {
        console.error(`❌ Failed to send to ${row.billing_email}:`, {
          error: emailErr.message,
          code: emailErr.code,
          command: emailErr.command
        });
      }
    }

    console.log("🎉 All emails processed.");
    return {
      statusCode: 200,
      body: JSON.stringify({
        message: 'Promotional emails sent successfully'
      })
    };
  } catch (error) {
    console.error("❌ Error occurred:", error);
    return {
      statusCode: 500,
      body: JSON.stringify({
        message: 'Error processing promotional emails',
        error: error.message
      })
    };
  } finally {
    if (client) client.release();
  }
};

// Helper function to convert stream to string
async function streamToString(stream) {
  const chunks = [];
  for await (const chunk of stream) {
    chunks.push(Buffer.from(chunk));
  }
  return Buffer.concat(chunks).toString('utf8');
}