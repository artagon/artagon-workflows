#include <iostream>
#include "src/greeter.h"

int main() {
    Greeter greeter;
    std::cout << greeter.greet() << std::endl;
    std::cout << greeter.greet("Alice") << std::endl;
    return 0;
}
