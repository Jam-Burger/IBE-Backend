#!/bin/bash

# Exit on error
set -e

echo "Building Lambda functions..."

# Build promotional_email_sender
echo "Building promotional_email_sender..."
cd "$(dirname "$0")/modules/lambda/promotional_email_sender"
yarn install --frozen-lockfile

# Build housekeeping_service
echo "Building housekeeping_service..."
cd "../housekeeping_service"
yarn install --frozen-lockfile

echo "All builds completed successfully!"

echo "Building SNS Lambda functions..."
cd "../../sns/lambda"
yarn install --frozen-lockfile