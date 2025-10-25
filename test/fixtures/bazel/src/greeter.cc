#include "src/greeter.h"

std::string Greeter::greet() const {
    return "Hello, World!";
}

std::string Greeter::greet(const std::string& name) const {
    if (name.empty()) {
        return greet();
    }
    return "Hello, " + name + "!";
}
