cmake_minimum_required(VERSION 3.20)
project(cmake_c_test_fixture VERSION 1.0.0 LANGUAGES C)

# Set C standard
set(CMAKE_C_STANDARD 17)
set(CMAKE_C_STANDARD_REQUIRED ON)

# Enable testing
enable_testing()

# Main executable
add_executable(hello_c src/main.c)

# Test executable
add_executable(test_hello src/test.c)

# Add test
add_test(NAME HelloTest COMMAND test_hello)

# Install targets
install(TARGETS hello_c DESTINATION bin)
