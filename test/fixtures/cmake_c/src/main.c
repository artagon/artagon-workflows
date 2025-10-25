#include <stdio.h>
#include <string.h>

/**
 * Returns a greeting message.
 *
 * NOTE: This implementation uses a static buffer for simplicity as this is
 * a minimal test fixture for workflow validation, not production code.
 * In production, use dynamic allocation or thread-local storage for thread safety.
 */
const char* greet(const char* name) {
    static char buffer[256];

    if (name == NULL || strlen(name) == 0) {
        return "Hello, World!";
    }

    snprintf(buffer, sizeof(buffer), "Hello, %s!", name);
    return buffer;
}

int main(void) {
    printf("%s\n", greet(NULL));
    printf("%s\n", greet("Alice"));
    return 0;
}
