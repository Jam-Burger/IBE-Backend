import nodemailer from 'nodemailer';
import {getPropertyDetails} from "./graphqlClient.mjs";

const transporter = nodemailer.createTransport({
    host: "smtp.gmail.com",
    port: 587,
    secure: false,
    auth: {
        user: process.env.EMAIL_USER,
        pass: process.env.EMAIL_PASS,
    }
});

const getAdminEmailsByPropertyId = async (client, propertyId) => {
    const query = `
        SELECT admin_email
        FROM admin
        WHERE property_id = $1
    `;
    const {rows} = await client.query(query, [propertyId]);
    return rows.map(row => row.admin_email);
}

export async function sendEmails(client, propertyId, data) {
    const emails = await getAdminEmailsByPropertyId(client, propertyId);
    const {property_name: propertyName} = await getPropertyDetails(propertyId);
    const emailSubject = `Staff Shortage Alert for ${propertyName}`;

    data = {
        ...data, propertyName
    };
    for (const email of emails) {
        const template = generateEmailTemplate(data);
        try {
            const info = await transporter.sendMail({
                from: `"Cleaning Scheduler" <${process.env.EMAIL_USER}>`,
                to: email,
                subject: emailSubject,
                html: template,
            });
            console.log(`📧 Email sent: ${info.messageId}`);
        } catch (emailError) {
            console.error("❌ Failed to send email:", emailError);
        }
    }
}