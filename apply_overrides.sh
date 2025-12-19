#!/bin/bash
set -e

SDK=/home/sdk/android11
OVR=/home/sdk/sdk_overrides

echo "Applying SDK overrides..."

rsync -av --checksum \
      --no-owner --no-group \
      "$OVR/" "$SDK/"

echo "Overrides applied successfully"
