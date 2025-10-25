cmake_minimum_required(VERSION 3.20)
project(cmake_cpp_test_fixture VERSION 1.0.0 LANGUAGES CXX)

# Set C++ standard
set(CMAKE_CXX_STANDARD 20)
set(CMAKE_CXX_STANDARD_REQUIRED ON)

# Enable testing
enable_testing()

# Main executable
add_executable(hello_cpp src/main.cpp)

# Test executable
add_executable(test_hello_cpp src/test.cpp)

# Add test
add_test(NAME HelloTestCpp COMMAND test_hello_cpp)

# Install targets
install(TARGETS hello_cpp DESTINATION bin)
