ALTER TABLE otp
    DROP CONSTRAINT otp_email_key;

ALTER TABLE guest_extension
    DROP CONSTRAINT guest_extension_traveler_email_key;

ALTER TABLE guest_extension
    ADD CONSTRAINT uc_guest_extension_billing_email UNIQUE (billing_email);