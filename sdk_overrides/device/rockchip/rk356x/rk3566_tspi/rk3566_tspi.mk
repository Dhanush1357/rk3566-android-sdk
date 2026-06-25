#
# Copyright 2014 The Android Open-Source Project
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#

# First lunching is R, api_level is 30
PRODUCT_SHIPPING_API_LEVEL := 30
PRODUCT_DTBO_TEMPLATE := $(LOCAL_PATH)/dt-overlay.in
PRODUCT_SDMMC_DEVICE := fe2b0000.dwmmc
PRODUCT_LOCALES := en_US


include device/rockchip/common/build/rockchip/DynamicPartitions.mk
include device/rockchip/rk356x/rk3566_tspi/BoardConfig.mk
include device/rockchip/common/BoardConfig.mk
$(call inherit-product, device/rockchip/rk356x/device.mk)
$(call inherit-product, device/rockchip/common/device.mk)
$(call inherit-product, frameworks/native/build/tablet-10in-xhdpi-2048-dalvik-heap.mk)

DEVICE_PACKAGE_OVERLAYS += $(LOCAL_PATH)/../overlay

PRODUCT_CHARACTERISTICS := tablet

PRODUCT_NAME := rk3566_tspi
PRODUCT_DEVICE := rk3566_tspi
PRODUCT_BRAND := ffvd
PRODUCT_MODEL := ffvd_66
PRODUCT_MANUFACTURER := ffvd
PRODUCT_AAPT_PREF_CONFIG := mdpi
#
## add Rockchip properties
#
#Screen density changed from 320 to 240, wucaicheng,1378913492@qq.com,20230817

PRODUCT_SYSTEM_PROPERTIES += \
    persist.sys.device_name=FFVD_66

PRODUCT_PROPERTY_OVERRIDES += ro.sf.lcd_density=240
PRODUCT_PROPERTY_OVERRIDES += ro.wifi.sleep.power.down=true
PRODUCT_PROPERTY_OVERRIDES += persist.wifi.sleep.delay.ms=0
PRODUCT_PROPERTY_OVERRIDES += persist.bt.power.down=true
PRODUCT_PROPERTY_OVERRIDES += ro.vendor.hdmirotationlock=true

# Disable emulator / cuttlefish / goldfish components
PRODUCT_SOONG_NAMESPACES :=
TARGET_BUILD_CUTTLEFISH := false


PRODUCT_PACKAGES_REMOVE += \
    libOpenglRender \
    libOpenglRender_passthrough \
    libvirglrenderer \
    libvirglrenderer_cuttlefish


# FFVD Nexus System Tool
PRODUCT_PACKAGES += FFVDNexus
