# Dockerfile for OS development with GCC cross-compiler
FROM ubuntu:latest

# Install base dependencies and cross-compiler build dependencies
RUN apt update && apt install -y \
    nasm gdb gcc make build-essential qemu-system-x86 grub-common grub-pc-bin xorriso mtools \
    curl bison flex libgmp3-dev libmpc-dev libmpfr-dev texinfo libisl-dev \
    && rm -rf /var/lib/apt/lists/*

# Copy and run the cross-compiler build script
COPY build_gcc_crosscompiler.sh /tmp/build_gcc_crosscompiler.sh
RUN cd /tmp && \
    chmod +x build_gcc_crosscompiler.sh && \
    ./build_gcc_crosscompiler.sh /opt/cross /tmp && \
    # Clean up build artifacts
    rm -rf /tmp/*

# Add cross-compiler to PATH for runtime
ENV PATH="/opt/cross/bin:$PATH"

WORKDIR /workspace
CMD ["/bin/bash"]
