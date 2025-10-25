#ifndef GREETER_H
#define GREETER_H

#include <string>

class Greeter {
public:
    std::string greet() const;
    std::string greet(const std::string& name) const;
};

#endif // GREETER_H
