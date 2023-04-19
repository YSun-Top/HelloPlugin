cmake_minimum_required(VERSION 3.4.1)

project("hello_so")
# 设置so文件的输出路径
# ${ANDROID_ABI} 表示的是CPU架构类型，比如arm64-v8a,x86
# 当前路径~/cpp/jniLibs/${ANDROID_ABI}
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/jniLibs/${ANDROID_ABI})

add_library( # Sets the name of the library.
        hello_so

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        hello_so.cpp)

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries( # Specifies the target library.
        hello_so

        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})