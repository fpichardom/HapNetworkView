# Use Amazon Corretto 8 with full JDK (includes JavaFX)
FROM amazoncorretto:8

# Install X11 and required dependencies
RUN yum update -y && yum install -y \
    libX11 \
    libXext \
    libXrender \
    libXtst \
    libXi \
    libXinerama \
    libXcursor \
    libXrandr \
    libXxf86vm \
    mesa-libGL \
    mesa-libGLU \
    fontconfig \
    freetype \
    maven \
    gtk2 \
    xorg-x11-xauth \
    && yum clean all \
    && rm -rf /var/cache/yum

# Create app directory
WORKDIR /app

# Copy the source code
COPY src /app/src
COPY pom.xml /app/
COPY Examples /app/Examples

# Make sure native libraries have execute permissions
RUN chmod +x /app/src/main/resources/dll_and_so/* || true

# Build the application
RUN mvn clean package -Dmaven.test.skip=true

# Set up entrypoint script
RUN echo '#!/bin/bash' > /app/entrypoint.sh && \
    echo 'echo "Testing X11 connection..."' >> /app/entrypoint.sh && \
    echo 'echo "DISPLAY=$DISPLAY"' >> /app/entrypoint.sh && \
    echo 'if [ -z "$DISPLAY" ]; then' >> /app/entrypoint.sh && \
    echo '  echo "ERROR: DISPLAY environment variable is not set."' >> /app/entrypoint.sh && \
    echo '  echo "Please set the DISPLAY variable before running the container."' >> /app/entrypoint.sh && \
    echo '  echo "See docker-readme.md for platform-specific instructions."' >> /app/entrypoint.sh && \
    echo '  exit 1' >> /app/entrypoint.sh && \
    echo 'fi' >> /app/entrypoint.sh && \
    echo 'echo "Starting HapNetworkView..."' >> /app/entrypoint.sh && \
    echo 'java -Djava.library.path=/app/src/main/resources/dll_and_so \' >> /app/entrypoint.sh && \
    echo '     -Dprism.order=sw \' >> /app/entrypoint.sh && \
    echo '     -Djavafx.verbose=true \' >> /app/entrypoint.sh && \
    echo '     -Dprism.verbose=true \' >> /app/entrypoint.sh && \
    echo '     -Djava.awt.headless=false \' >> /app/entrypoint.sh && \
    echo '     -jar target/HapNetworkView-1.0.jar' >> /app/entrypoint.sh && \
    chmod +x /app/entrypoint.sh

# Use the entrypoint script
ENTRYPOINT ["/app/entrypoint.sh"]