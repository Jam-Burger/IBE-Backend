#!/bin/bash

echo "Checking for existing ZIP files..."

if [ ! -f "housekeeping_service.zip" ] || [ ! -f "promotional_email_sender.zip" ]; then
    echo "One or both ZIP files missing. Starting build process..."
    
    # Housekeeping service
    echo "Building housekeeping service..."
    cd housekeeping_service
    npm install --production
    cd ..
    cd housekeeping_service && zip -r ../housekeeping_service.zip . && cd ..
    
    # Promotional email sender
    echo "Building promotional email sender..."
    cd promotional_email_sender
    npm install --production
    cd ..
    cd promotional_email_sender && zip -r ../promotional_email_sender.zip . && cd ..
    
    echo "Build complete. ZIP files created."
else
    echo "ZIP files exist. No rebuild needed."
fi 