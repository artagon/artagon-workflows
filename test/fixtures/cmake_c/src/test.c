#include <stdio.h>
#include <string.h>
#include <assert.h>

/* Forward declaration */
const char* greet(const char* name);

/*
 * Inline the greet function for testing.
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
    const char* result;

    /* Test 1: Basic greeting */
    result = greet(NULL);
    assert(strcmp(result, "Hello, World!") == 0);
    printf("✓ Test 1 passed: Basic greeting\n");

    /* Test 2: Personalized greeting */
    result = greet("Alice");
    assert(strcmp(result, "Hello, Alice!") == 0);
    printf("✓ Test 2 passed: Personalized greeting\n");

    /* Test 3: Empty name */
    result = greet("");
    assert(strcmp(result, "Hello, World!") == 0);
    printf("✓ Test 3 passed: Empty name\n");

    printf("\nAll tests passed!\n");
    return 0;
}
