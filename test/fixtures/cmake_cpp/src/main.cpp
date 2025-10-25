#include <iostream>
#include <string>

/**
 * Simple greeting class for workflow testing.
 */
class Greeter {
public:
    /**
     * Returns a greeting message.
     */
    std::string greet() const {
        return "Hello, World!";
    }

    /**
     * Returns a personalized greeting message.
     *
     * @param name the name to greet
     * @return personalized greeting string
     */
    std::string greet(const std::string& name) const {
        if (name.empty()) {
            return greet();
        }
        return "Hello, " + name + "!";
    }
};

int main() {
    Greeter greeter;
    std::cout << greeter.greet() << std::endl;
    std::cout << greeter.greet("Alice") << std::endl;
    return 0;
}
