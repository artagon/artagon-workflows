package org.artagon.test;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test cases for Hello class.
 */
class HelloTest {

    @Test
    @DisplayName("Test basic greeting")
    void testGreet() {
        Hello hello = new Hello();
        assertEquals("Hello, World!", hello.greet());
    }

    @Test
    @DisplayName("Test personalized greeting")
    void testGreetWithName() {
        Hello hello = new Hello();
        assertEquals("Hello, Alice!", hello.greet("Alice"));
    }

    @Test
    @DisplayName("Test greeting with null name")
    void testGreetWithNull() {
        Hello hello = new Hello();
        assertEquals("Hello, World!", hello.greet(null));
    }

    @Test
    @DisplayName("Test greeting with empty name")
    void testGreetWithEmpty() {
        Hello hello = new Hello();
        assertEquals("Hello, World!", hello.greet(""));
        assertEquals("Hello, World!", hello.greet("   "));
    }
}
