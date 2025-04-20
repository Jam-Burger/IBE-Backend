#!/bin/bash

# Function to clean and install dependencies
build_lambda() {
    local lambda_dir=$1
    echo "Building $lambda_dir..."
        cd "$lambda_dir" || exit
    
    # Clean existing node_modules
    rm -rf node_modules
    rm -f yarn.lock
    
    # Install dependencies
    yarn install --production
    cd - || exit
}

# Build both lambda functions
build_lambda "housekeeping_service"
build_lambda "promotional_email_sender"

echo "Build complete!" 