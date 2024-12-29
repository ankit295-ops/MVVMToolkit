#!/bin/bash

echo "Setting up the JitPack build environment for Android..."

# Exit immediately if a command exits with a non-zero status
set -e

# Update Gradle Wrapper to a specific version if necessary
echo "Updating Gradle Wrapper..."
./gradlew wrapper --gradle-version 7.5 --distribution-type all

# Clean the build directory
echo "Cleaning previous build outputs..."
./gradlew clean

# Resolve dependencies
echo "Resolving dependencies..."
./gradlew dependencies

echo "Environment setup complete!"