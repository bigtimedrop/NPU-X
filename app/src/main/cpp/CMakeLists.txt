cmake_minimum_required(VERSION 3.22.1)

project("nnapi_devices")

add_library(
    nnapi_devices
    SHARED
    nnapi_devices.cpp
)

find_library(
    log-lib
    log
)

find_library(
    android-lib
    android
)

target_link_libraries(
    nnapi_devices
    ${log-lib}
    ${android-lib}
    neuralnetworks
)
