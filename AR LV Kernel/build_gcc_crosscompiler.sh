#!/bin/bash
set -e # exit on error

# Script to build a GCC cross-compiler and binutils that target the x86_64-elf architecture.
# Source: https://wiki.osdev.org/GCC_Cross-Compiler#Preparing_for_the_build
# Usage: ./build_gcc_crosscompiler.sh [PREFIX] [BUILD_DIR]
#   PREFIX: Installation directory (default: ./cross_compiler)
#   BUILD_DIR: Directory for intermediate build artifacts (default: current directory)

# Install dependencies
apt install -y curl make gcc g++ build-essential bison flex libgmp3-dev libmpc-dev libmpfr-dev texinfo libisl-dev

# Set up variables
default_dir=$(pwd) # directory where script was started
PREFIX="${1:-$default_dir/cross_compiler}" # directory where the built GCC and binutils will be stored
BUILD_DIR="${2:-$default_dir}" # directory for intermediate build artifacts
TARGET="x86_64-elf" # target architecture
export PATH="$PREFIX/bin:$PATH" # add built binaries to PATH

# Download GCC and binutils source code
# Tested with 
#   gcc-15.2.0, binutils-2.45.1
#   gcc-14.2.0, binutils-2.43.1
#   gcc-11.2.0, binutils-2.37

binutils_version="binutils-2.45.1"
gcc_version="gcc-15.2.0"
cd "$BUILD_DIR"
curl -L -O https://ftpmirror.gnu.org/gnu/binutils/$binutils_version.tar.gz
curl -L -O https://ftpmirror.gnu.org/gnu/gcc/$gcc_version/$gcc_version.tar.gz

# Extract GCC source code and binutils source code
cd "$BUILD_DIR"
tar xf $binutils_version.tar.gz
tar xf $gcc_version.tar.gz

# Download GCC prerequisites. 
# Relevant script must be started in GCC source code root directory.
cd "$BUILD_DIR/$gcc_version"
./contrib/download_prerequisites

# Create a build directory for binutils and build binutils
cd "$BUILD_DIR"
mkdir build-binutils
cd build-binutils
../$binutils_version/configure --target=$TARGET --prefix="$PREFIX" --with-sysroot --disable-nls --disable-werror
make -j$(nproc)
make install

# NOTES:
# - The --with-sysroot flag is used to tell the compiler that the target system is not the same as the host system.

# Create a build directory for GCC and build GCC
cd "$BUILD_DIR"
# The $PREFIX/bin dir _must_ be in the PATH. We did that above.
which -- $TARGET-as || echo $TARGET-as is not in the PATH
mkdir build-gcc
cd build-gcc
../$gcc_version/configure --target=$TARGET --prefix="$PREFIX" --disable-nls --enable-languages=c,c++ --without-headers
make -j$(nproc) all-gcc
make -j$(nproc) all-target-libgcc
make install-gcc
make install-target-libgcc
