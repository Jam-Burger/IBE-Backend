#!/bin/bash

# Exit on error
set -e

echo "Building Lambda functions..."

# Build promotional_email_sender
echo "Building promotional_email_sender..."
cd "$(dirname "$0")/promotional_email_sender"
yarn install --frozen-lockfile
zip -r "../promotional_email_sender.zip" . -x "node_modules/.cache/*" "*.git*"

cd ..

# Build housekeeping_service
echo "Building housekeeping_service..."
cd "$(dirname "$0")/housekeeping_service"
yarn install --frozen-lockfile
zip -r "../housekeeping_service.zip" . -x "node_modules/.cache/*" "*.git*"

echo "All builds completed successfully!" 