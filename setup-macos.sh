#!/bin/bash

# Simple script to set up the .env file for macOS users

echo "Setting up .env file for macOS..."

# Try to find the IP address
IP=""
for interface in en0 en1 en2 en3; do
    if ifconfig $interface &>/dev/null; then
        TEMP_IP=$(ifconfig $interface | grep inet | grep -v inet6 | awk '$1=="inet" {print $2}')
        if [ ! -z "$TEMP_IP" ]; then
            IP=$TEMP_IP
            echo "Found IP address $IP on interface $interface"
            break
        fi
    fi
done

if [ -z "$IP" ]; then
    echo "Could not automatically determine your IP address."
    echo "Please enter your IP address manually:"
    read -p "> " IP
    
    if [ -z "$IP" ]; then
        echo "No IP address provided. Exiting."
        exit 1
    fi
fi

# Create .env file
echo "Creating .env file with DISPLAY=$IP:0"
echo "DISPLAY=$IP:0" > .env

echo "Setup complete!"
echo ""
echo "Next steps:"
echo "1. Make sure XQuartz is running with 'Allow connections from network clients' enabled"
echo "2. Run: xhost + $IP"
echo "3. Run: docker-compose build"
echo "4. Run: docker-compose run --rm hapnetworkview"