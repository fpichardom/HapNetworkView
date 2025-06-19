# Running HapNetworkView with Docker

This guide explains how to run HapNetworkView using Docker, which allows you to run the application without installing Java or any dependencies directly on your system.

## Prerequisites

1. [Docker](https://www.docker.com/products/docker-desktop) installed on your system
2. An X11 server installed on your system (for GUI display)

## Quick Start

Follow the instructions for your operating system below. Each platform requires:
1. Setting up an X11 server
2. Creating a .env file with the correct DISPLAY setting
3. Running Docker Compose

### Windows

1. Install an X11 server like [VcXsrv](https://sourceforge.net/projects/vcxsrv/)
2. Start VcXsrv with:
   - Multiple windows
   - Display number: 0
   - Start no client
   - **Disable access control** (important!)
   - **Disable Native OpenGL** (very important for JavaFX!)
3. Create a .env file with the Windows display setting:
   ```
   echo DISPLAY=host.docker.internal:0.0 > .env
   ```
4. Run Docker Compose:
   ```
   docker-compose build
   docker-compose run --rm hapnetworkview
   ```

### macOS

#### Option 1: Using the setup script (recommended)

1. Install [XQuartz](https://www.xquartz.org/)
2. Start XQuartz
3. Open XQuartz preferences and enable "Allow connections from network clients"
4. Restart XQuartz
5. Run the setup script:
   ```bash
   chmod +x setup-macos.sh
   ./setup-macos.sh
   ```
6. Follow the instructions provided by the script

#### Option 2: Manual setup

1. Install [XQuartz](https://www.xquartz.org/)
2. Start XQuartz
3. Open XQuartz preferences and enable "Allow connections from network clients"
4. Restart XQuartz
5. Find your IP address:
   ```bash
   ifconfig en0 | grep inet | awk '$1=="inet" {print $2}'
   ```
   Example output: `192.168.1.5`
6. Create a .env file with your IP:
   ```bash
   echo "DISPLAY=192.168.1.5:0" > .env
   ```
   (Replace 192.168.1.5 with your actual IP address)
7. Allow connections from your IP:
   ```bash
   xhost + 192.168.1.5
   ```
   (Replace 192.168.1.5 with your actual IP address)
8. Run Docker Compose:
   ```bash
   docker-compose build
   docker-compose run --rm hapnetworkview
   ```

### Linux

1. Allow X11 connections:
   ```bash
   xhost +local:docker
   ```
2. Create a .env file with the Linux display setting:
   ```bash
   echo DISPLAY=$DISPLAY > .env
   ```
3. Run Docker Compose:
   ```bash
   docker-compose build
   docker-compose run --rm --network=host hapnetworkview
   ```

## How It Works

The Docker setup will:

1. Use Amazon Corretto 8 as the base image (which includes JavaFX)
2. Install necessary dependencies including Maven and X11 libraries
3. Copy the source code into the container
4. Build the application using Maven inside the container
5. Run the application with X11 forwarding to display the GUI on your host machine

## Data Persistence

The Docker setup includes persistent volumes for your data:

1. **Examples**: Contains example files for testing
   - Path in container: `/app/Examples`
   - Path on host: `./Examples`

2. **Input**: Place your input files here
   - Path in container: `/app/input`
   - Path on host: `./input`

3. **Output**: Results and saved files will appear here
   - Path in container: `/app/output`
   - Path on host: `./output`

Any files you place in the `input` directory will be accessible from within the application. Similarly, any files saved by the application to the `output` directory will be available on your host machine.

## Troubleshooting

### No GUI appears

- Make sure your X11 server is running and properly configured
- On Windows:
  - Ensure VcXsrv is running with "Disable access control" and "Disable Native OpenGL" options
  - Check that your firewall allows VcXsrv to communicate on both private and public networks
- On Mac:
  - Ensure you've allowed connections from network clients in XQuartz preferences
  - Make sure you've used the correct IP address in the .env.macos file
  - Try running `xhost +` to allow all connections (for testing only)
- On Linux:
  - Try running with `--network=host` if not already doing so

### Permission Issues

If you encounter permission issues with the X11 socket, try:

```bash
# For Linux
xhost +local:docker

# For macOS
xhost +
```

### File Access Issues

If you encounter issues accessing or saving files:

1. **Permission issues**: The container runs as root by default. If you're having permission issues with the mounted volumes, you can change the ownership of the directories:
   ```bash
   # For Linux/macOS
   sudo chown -R $USER:$USER input output
   ```

2. **Custom mount points**: If you need to mount additional directories, you can modify the docker-compose.yml file:
   ```yaml
   volumes:
     - /path/on/host:/path/in/container
   ```

3. **File not found**: Make sure you're looking in the correct directory. Inside the container, your files are at:
   - `/app/input` for input files
   - `/app/output` for output files
   - `/app/Examples` for example files