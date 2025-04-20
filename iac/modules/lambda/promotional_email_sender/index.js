const AWSXRay = require('aws-xray-sdk-core');
const { Pool } = require('pg');
const nodemailer = require('nodemailer');
const AWS = require('aws-sdk');
const { generateEmailHtml } = require('./generateTemplate');

// Capture and trace AWS SDK calls
const awsSDK = AWSXRay.captureAWS(AWS);
const s3 = new awsSDK.S3();

// Enable X-Ray tracing for PostgreSQL
const pgPool = AWSXRay.capturePostgres(new Pool({
  host: process.env.DB_HOST,
  port: 5432,
  user: process.env.DB_USER,
  password: process.env.DB_PASSWORD,
  database: process.env.DB_NAME,
  ssl: { rejectUnauthorized: false }
}));

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
  // Get the current segment
  const segment = AWSXRay.getSegment();
  
  const record = event.Records[0];
  const bucket = record.s3.bucket.name;
  const key = decodeURIComponent(record.s3.object.key.replace(/\+/g, ' '));

  let client;

  try {
    // Create subsegment for S3 operations
    const s3Segment = segment.addNewSubsegment('s3-operations');
    const s3Object = await s3.getObject({ Bucket: bucket, Key: key }).promise();
    const offerJson = JSON.parse(s3Object.Body.toString('utf-8'));
    s3Segment.close();

    console.log(offerJson);

    // Create subsegment for database operations
    const dbSegment = segment.addNewSubsegment('database-operations');
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
    dbSegment.close();

    // Create subsegment for email operations
    const emailSegment = segment.addNewSubsegment('email-operations');
    
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
        emailSegment.addError(verifyErr);
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
        if (emailSegment) {
          emailSegment.addError(emailErr);
        }
      }
    }
    emailSegment.close();

    console.log("🎉 All emails processed.");
  } catch (error) {
    console.error("❌ Error occurred:", error);
    if (segment) {
      segment.addError(error);
    }
    throw error;
  } finally {
    if (client) client.release();
  }
};