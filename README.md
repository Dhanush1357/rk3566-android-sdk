# RK3566 Android SDK

This repository provides a containerized environment for building and developing with the RK3566 Android SDK. It leverages Docker to ensure a consistent build environment based on Ubuntu 18.04 with all necessary dependencies installed.

## Prerequisites

Before getting started, ensure you have the following installed on your host machine:

- [Docker Engine](https://docs.docker.com/engine/install/)
- [Docker Compose](https://docs.docker.com/compose/install/)
- tspi_android_sdk_20230909 Downloaded and extracted to the root of this repository.

## Project Structure

```text
rk3566_android_sdk/
├── Dockerfile
├── docker-compose.yml
├── tspi_android_sdk_20230909/   # Android SDK source (host-mounted)
└── README.md
```

## Platform Handling (Important)

### Default behavior

Docker Compose uses the host architecture automatically.

### Apple Silicon / Cross-build (Recommended)

Some Android dependencies (e.g. `lib32stdc++6`) require x86_64.
You can optionally force the platform at runtime:

```bash
ANDROID_PLATFORM=linux/amd64 docker compose up -d
```

If not specified, Docker uses the default platform.

## Setup Instructions

### Build the Docker Image

Build the image defined by the `Dockerfile`:

```bash
docker compose build
```

### Start the Container

Start the container in detached mode:

```bash
docker compose up -d
```

Or with platform override (Apple Silicon):

```bash
ANDROID_PLATFORM=linux/amd64 docker compose up -d
```

### Verify Container Status

```bash
docker compose ps
```

Expected output:

```text
rk3566_android11   running
```

## Usage

### Accessing the Container Shell

```bash
docker exec -it rk3566_android11 bash
```

### SDK Source Location

The Android SDK is mounted from the host:

```bash
cd ~/android11
```

This maps to:
`./tspi_android_sdk_20230909` (on host)

Changes are reflected instantly on both sides.

## SDK Directory Structure

| Directory/File     | Description                                                                          |
| :----------------- | :----------------------------------------------------------------------------------- |
| `Android.bp`       | Blueprint build file for Android development, used to define and configure modules.  |
| `art`              | Contains code related to the Android Runtime (ART).                                  |
| `bionic`           | Contains implementations of C libraries for Android systems.                         |
| `bootable`         | Contains boot-related code such as bootloader and boot images.                       |
| `bootstrap.bash`   | Script for starting the Android development environment.                             |
| `build`            | Contains code and scripts related to the build system.                               |
| `build.sh`         | Script for building the Android system.                                              |
| `compatibility`    | Contains code related to Android Compatibility Test Suite (CTS).                     |
| `cts`              | Code and configuration for Android Compatibility Test Suite (CTS).                   |
| `development`      | Contains development tools and libraries like SDK Manager and emulator.              |
| `device`           | Contains manufacturer or platform-specific code and configuration files.             |
| `external`         | Contains third-party libraries and tools from external sources.                      |
| `frameworks`       | Contains code for the Android system framework.                                      |
| `hardware`         | Contains hardware-related code such as drivers and Hardware Abstraction Layer (HAL). |
| `javaenv.sh`       | Script to set up the Java runtime environment.                                       |
| `kernel`           | Contains Linux kernel code used by the Android system.                               |
| `libcore`          | Contains implementations of Java core libraries.                                     |
| `libnativehelper`  | Contains libraries for implementing interaction between native code and Java code.   |
| `makefile`         | Makefile used for building the Android system.                                       |
| `mkcombinedroot`   | Script for creating a combined root filesystem.                                      |
| `mkimage_ab.sh`    | Script for creating Android system boot images.                                      |
| `mkimage.sh`       | Script for creating RK3566 upgrade images.                                           |
| `out`              | Directory for output files generated during the build process.                       |
| `packages`         | Contains code for pre-installed applications and services.                           |
| `pdk`              | Contains files related to the Android Platform Development Kit (PDK).                |
| `platform_testing` | Contains test code running on the device.                                            |
| `prebuilts`        | Contains precompiled third-party tools and libraries.                                |
| `rkbin`            | Contains the binary files required for RK3566 system development.                    |
| `RKDocs`           | Contains documentation for RK3566 hardware development.                              |
| `rkst`             | Contains files related to RK development toolchain.                                  |
| `sdk`              | Contains files for the Android Software Development Kit (SDK).                       |
| `system`           | Contains core functionality and services of the Android system.                      |
| `test`             | Contains various test code and testing frameworks.                                   |
| `tools`            | Contains various utilities for Android development.                                  |
| `toolchain`        | Contains files related to the cross-compilation toolchain.                           |
| `u-boot`           | Contains U-Boot bootloader code and configuration files.                             |
| `vendor`           | Contains manufacturer-specific code and configuration files.                         |
| `rockdev`          | Stores compiled output firmware.                                                     |

Among these directories, we often modify the `u-boot`, `kernel`, `vendor`, `system`, `device`, `frameworks`, and `packages` directories.

## Full SDK Compilation (Recommended Flow)

### Build U-Boot

```bash
cd u-boot
./make.sh rk3566
```

### Build Kernel

```bash
cd ../kernel
make clean
make distclean
make ARCH=arm64 tspi_defconfig rk356x_evb.config android-11.config
make ARCH=arm64 tspi-rk3566-user-v10.img -j$(nproc)
cd ..
```

### Build Android System

```bash
# If you open a new terminal window, you need to first configure the environment. Run this command once, and it will remain active as long as you don't close the window:
source build/envsetup.sh
lunch rk3566_tspi-userdebug

# Build commands
make installclean -j$(nproc)
make -j$(nproc)
```

### Generate Firmware Images

```bash
./mkimage.sh
```

### Generate update.img (Single Firmware Package)

```bash
./build.sh -u
```

### Generated Images

The image files will be generated in the `rockdev/Image-rk3566_tspi` directory.

| Image Name          | Description                                                                                              |
| :------------------ | :------------------------------------------------------------------------------------------------------- |
| `baseparameter.img` | Stores display configuration information (resolution, etc.). Flashed to `baseparameter` partition.       |
| `boot-debug.img`    | Similar to boot.img but allows root access debugging.                                                    |
| `boot.img`          | Android boot image (ramdisk, kernel Image, DTB, resource.img). Flashed to `boot` partition.              |
| `config.cfg`        | Configuration file for Rockchip's downloader tool.                                                       |
| `dtbo.img`          | Device Tree Overlays (DTO) for Android 10+. Flashed to `dtbo` partition.                                 |
| `MiniLoaderAll.bin` | Loader code running before U-Boot (TPL/SPL).                                                             |
| `misc.img`          | Bootloader Control Block (BCB) and A/B system data. Flashed to `misc` partition.                         |
| `parameter.txt`     | Partition table definition. Not flashed directly but defines partition layout.                           |
| `recovery.img`      | Recovery mode image (recovery-ramdisk, kernel, DTB/DTBO). Flashed to `recovery` partition.               |
| `resource.img`      | Contains logo and DTB. Bundled in boot/recovery, not flashed separately.                                 |
| `super.img`         | Super image containing `odm`, `product`, `system`, `system_ext`, `vendor`. Flashed to `super` partition. |
| `uboot.img`         | Combined Trust + U-Boot image. Flashed to `uboot` partition.                                             |
| `vbmeta.img`        | Android Verified Boot (AVB) verification info.                                                           |
| `update.img`        | combines all the images into a single `update.img` image.                                                |

### Module-Level Build

```bash
mm    # Compile the module in the current directory (the directory must contain an Android.mk file)
mmm   # Compile a module in a specified path (the specified path must contain an Android.mk file)
```

## Useful Docker Commands

### Stop the Container

```bash
docker compose stop
```

### Restart the Container

```bash
docker compose start
```

### Stop and Remove Container

```bash
docker compose down
```

### Rebuild Image (after Dockerfile changes)

```bash
docker compose build --no-cache
```

## Troubleshooting

### ❌ Unable to locate package lib32stdc++6

**Cause**: Building on ARM64 (Apple Silicon) without platform override.
**Fix**:

```bash
ANDROID_PLATFORM=linux/amd64 docker compose build --no-cache
```

### ❌ Permission Issues in SDK Directory

Ensure the SDK directory is writable by uid 1000:

```bash
sudo chown -R 1000:1000 tspi_android_sdk_20230909
```
