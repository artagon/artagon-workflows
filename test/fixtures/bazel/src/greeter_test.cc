#include "src/greeter.h"
#include <cassert>
#include <iostream>

int main() {
    Greeter greeter;

    // Test 1: Basic greeting
    assert(greeter.greet() == "Hello, World!");
    std::cout << "✓ Test 1 passed: Basic greeting" << std::endl;

    // Test 2: Personalized greeting
    assert(greeter.greet("Alice") == "Hello, Alice!");
    std::cout << "✓ Test 2 passed: Personalized greeting" << std::endl;

    // Test 3: Empty name
    assert(greeter.greet("") == "Hello, World!");
    std::cout << "✓ Test 3 passed: Empty name" << std::endl;

    std::cout << "\nAll tests passed!" << std::endl;
    return 0;
}
