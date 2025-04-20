const { Pool } = require('pg');
const nodemailer = require('nodemailer');
const AWS = require('aws-sdk');
const { generateEmailHtml } = require('./generateTemplate');

const s3 = new AWS.S3();

const pool = new Pool({
  host: process.env.DB_HOST,
  port: 5432,
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
    user: process.env.EMAIL_USER,
    pass: process.env.EMAIL_PASS,
  },
});

exports.handler = async (event) => {
  const record = event.Records[0];
  const bucket = record.s3.bucket.name;
  const key = decodeURIComponent(record.s3.object.key.replace(/\+/g, ' '));

  let client;

  try {
    // Fetch JSON from S3
    const s3Object = await s3.getObject({ Bucket: bucket, Key: key }).promise();
    const offerJson = JSON.parse(s3Object.Body.toString('utf-8'));

    console.log(offerJson);

    client = await pool.connect();

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

    // Generate email HTML
    const emailHtml = generateEmailHtml(offerJson);

    // Query emails from DB
    const res = await client.query(`
      SELECT billing_email 
      FROM guest_extension 
      WHERE special_offers_consent = true AND billing_email IS NOT NULL
    `);

    for (let row of res.rows) {
      try {
        const info = await transporter.sendMail({
          from: process.env.EMAIL_USER,
          to: row.billing_email,
          subject: `🎁 ${offerJson.title} - Limited Time Offer!`,
          html: emailHtml,
        });
        console.log(`✅ Email sent to ${row.billing_email}`);
      } catch (emailErr) {
        console.error(`❌ Failed to send to ${row.billing_email}:`, emailErr);
      }
    }

    console.log("🎉 All emails processed.");
  } catch (err) {
    console.error("❌ Error occurred:", err);
    throw err;
  } finally {
    if (client) client.release();
  }
};